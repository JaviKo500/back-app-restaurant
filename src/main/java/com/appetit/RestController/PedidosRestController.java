package com.appetit.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appetit.models.Cliente;
import com.appetit.models.Estado;
import com.appetit.models.Pedido;
import com.appetit.service.ClienteService;
import com.appetit.service.EstadoService;
import com.appetit.service.PedidoService;

@RestController
@CrossOrigin("*")
@RequestMapping("/")
public class PedidosRestController {

	@Autowired
	PedidoService pedidoService;

	@Autowired
	ClienteService clienteService;

	@Autowired
	EstadoService estadoService;

	@GetMapping("pedidos/dia/estado/{estado_id}")
	public ResponseEntity<?> obtenerPerdidosDiaEstado(@PathVariable Long estado_id) {
		Map<String, Object> response = new HashMap<>();
		List<Pedido> pedidos = new ArrayList<>();
		Estado estado = null;
		try {
			estado = estadoService.buscarEstadoByid(estado_id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error el obtener el estado.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (estado == null) {
			response.put("mensaje", "El estado no existe no se puede obtener los pedidos.");
			response.put("error", "Error de id de estado.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			pedidos = pedidoService.obtenerPedidosDiaFechaAndEstado(new Date(), estado);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error el obtener los pedidos.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Pedidos obtenidos.");
		response.put("pedidos", pedidos);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@PostMapping("register/new/pedido")
	public ResponseEntity<?> registrarClientePedido(@RequestBody Pedido pedido) {
		Map<String, Object> response = new HashMap<>();
		Pedido ped = null;
		if (pedido == null) {
			response.put("mensaje", "El pedido no contiene valores válidos");
			response.put("error", "pedido nulo.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_GATEWAY);
		}
		// ver si el pedido tiene items
		if (pedido.getItems().size() == 0) {
			response.put("mensaje", "El pedido no contiene productos.");
			response.put("error", "Pedido incompleto.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_GATEWAY);
		}
		// asignar un estado de Solicitado al pedido
		Estado estado = null;
		try {
			estado = estadoService.buscarEstadoByNombre("Solicitado");
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al asignar el estado a su pedido.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (estado != null) {
			pedido.setEstado(estado);
		} else {
			response.put("mensaje", "No se pudo asignar un estado al pedido.");
			response.put("error", "Error de estado no encontrado.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		// verificar si el pedido es como usuario final
		if (pedido.getCliente() == null) {
			Cliente cliente = null;
			try {
				cliente = clienteService.FindClineteByCedula("9999999999");
			} catch (DataAccessException e) {
				response.put("mensaje", "Error al asignar el consumidor final");
				response.put("error", e.getMostSpecificCause().getMessage());
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			if (cliente != null) {
				pedido.setCliente(cliente);
			} else {
				response.put("mensaje", "Error al asignar el consumidor final");
				response.put("error", "Consumidor final no encontrado en la base de datos.");
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
		}

		// finalmente registrar el pedido del cliente
		try {
			ped = pedidoService.registrarNuevoPedido(pedido);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al registrar el pedido.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Pedido registrado.");
		response.put("id_pedido", ped.getId());
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

}
