package com.appetit.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appetit.models.ArqueoCaja;
import com.appetit.models.Estado;
import com.appetit.models.MedioPago;
import com.appetit.models.MovimientoCaja;
import com.appetit.models.Pedido;
import com.appetit.models.Usuario;
import com.appetit.service.ArqueoService;
import com.appetit.service.EstadoService;
import com.appetit.service.MovimientoService;
import com.appetit.service.PedidoService;
import com.appetit.service.UsuarioService;
import com.appetit.service.ValidacionesMovimientosService;

@RestController
@CrossOrigin("*")
@RequestMapping("/")
public class MovimientoRestController {

	@Autowired
	MovimientoService movimientoService;

	@Autowired
	PedidoService pedidoService;

	@Autowired
	ValidacionesMovimientosService validacionMov;

	@Autowired
	EstadoService estadoService;

	@Autowired
	UsuarioService UsuarioService;

	@Autowired
	ArqueoService arqueoService;

	// obtener lista de medios de pago
	@Secured({ "ROLE_ADMIN" })
	@GetMapping("get/medio-pago")
	public List<MedioPago> obtenerMediosDePago() {
		return movimientoService.ObtenerMediosDePago();
	}

	@Secured({ "ROLE_ADMIN" })
	@PostMapping("finalizar-pedido/crear-movimiento")
	public ResponseEntity<?> TerminarPedidoNewMovimientoPedido(@RequestBody MovimientoCaja movimiento) {
		System.out.print("hola");
		Map<String, Object> response = new HashMap<>();
		List<String> errores = validacionMov.finalizarPedidoMovimientos(movimiento);
		Usuario usuario = null;
		ArqueoCaja arqueo;
		if (errores.size() != 0) {
			response.put("mensaje", "errores al finalizar pedido");
			response.put("error", "error de campos");
			response.put("errores", errores);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}
		if (movimiento.getId() != null) {
			response.put("mensaje", "Movimiento no editable.");
			response.put("error", "No se puede realizar cambios una ves generado.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		try {
			usuario = UsuarioService.buscarusuarioByIdMovimiento(movimiento.getUsuario().getId());

		} catch (DataAccessException e) {
			response.put("mensaje", "Error con el usuario");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		if (usuario == null) {
			response.put("mensaje", "Error con el usuario.");
			response.put("error", "El usaurio no existe o identificación errónea.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			arqueo = arqueoService.getArqueoByUsuarioIsOpen(usuario);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al buscar arqueo de caja.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		if (arqueo == null) {
			response.put("mensaje", "No cuenta con un arqueo abierto.");
			response.put("error", "Arqueo no encontrado para el usuario: " + usuario.getUsername());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		Pedido p = movimiento.getPedido();
		Estado estado = null;
		if (p.getEstado().getId() != 5) {
			estado = estadoService.buscarEstadoByid(4L);
			p.setEstado(estado);
			p.setIsEntregado(true);
			movimiento.setMonto(p.getTotal());
		} else {
			p.setIsAnulado(true);
		}

		try {
			movimiento.setFecha(new Date());
			movimiento.setPedido(p);
			pedidoService.registrarNuevoPedido(movimiento.getPedido());
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al finalizar el pedido.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		try {
			movimiento.setFechaMovimiento(new Date());
			movimiento.setUsuario(usuario);
			movimientoService.registrarMovimiento(movimiento);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al finalizar el pedido y generar movimiento de caja.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (movimiento.getTipoPago().getId() == 1) {
			arqueo.setEfectivo(arqueo.getEfectivo() + movimiento.getMonto());
		} else {
			arqueo.setBancos(arqueo.getBancos() + movimiento.getMonto());
		}
		// ******************************************terminar validaciones de arqueo
		// DIFERENCIA,
		try {
			arqueo.getMovimientos().add(movimiento);
			arqueoService.registrarNuevoArqueo(arqueo);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al registrar movimiento");
			response.put("error",
					"Error al registrar un movimiento en el arqueo de caja.\n" + e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// validar el tipo de transaccion y agregar al arqueo
		response.put("mensaje", "Correcto.");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@GetMapping("get/movimientos/id_usuario/{id}/page/{page}")
	public ResponseEntity<?> obtenerMovimientosFechaUsuario(@PathVariable Long id, @PathVariable Integer page) {
		Map<String, Object> response = new HashMap<>();
		Usuario user = null;
		Page<MovimientoCaja> movimientos;
		try {
			// busqueda de usuario si no esta eliminado, estado true e ID
			user = UsuarioService.buscarusuarioByIdMovimiento(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al obtener el usuario");
			response.put("error", "Error de id de usuario");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (user == null) {
			response.put("mensaje", "Su sessión tiene problemas.");
			response.put("error", "Su id de usuario pudo haber sido eliminado o ddeshabilitado.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			Pageable pageable = PageRequest.of(page, 10);
			movimientos = movimientoService.obtenerMovimientosCajaUsuarioFecha(pageable, user);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al obtener los movimientos");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("movimientos", movimientos);
		response.put("mensaje", "movimientos obtenidos");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	// no finalizado /***********************************************************
	/*
	 * @PostMapping("registrar/movimiento-caja/") public ResponseEntity<?>
	 * registrarMovimiento(@RequestBody MovimientoCaja movimiento) { Map<String,
	 * Object> response = new HashMap<>(); List<String> errores =
	 * validacionMov.validacionesMovimientos(movimiento); if (errores.size() != 0) {
	 * response.put("mensaje", "errores al finalizar pedido"); response.put("error",
	 * "error de campos"); response.put("errores", errores); return new
	 * ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT); } //
	 * procesar el pedido try { Pedido p = movimiento.getPedido(); // Estado est //
	 * p.setEstado(estado);
	 * pedidoService.registrarNuevoPedido(movimiento.getPedido()); } catch
	 * (DataAccessException e) { response.put("mensaje",
	 * "Error al finalizar el pedido."); response.put("error",
	 * e.getMostSpecificCause().getMessage()); return new ResponseEntity<Map<String,
	 * Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR); } try {
	 * movimientoService.registrarMovimiento(movimiento); } catch
	 * (DataAccessException e) { response.put("mensaje",
	 * "Error al procesar el movimiento"); response.put("error",
	 * e.getMostSpecificCause().getMessage()); return new ResponseEntity<Map<String,
	 * Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR); }
	 * response.put("mensaje", "movimiento correcto"); return new
	 * ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED); }
	 */
}
