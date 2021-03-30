package com.appetit.RestController;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appetit.models.Caja;
import com.appetit.service.CajaService;
import com.appetit.service.ValidacionService;

@RestController
@CrossOrigin("*")
@RequestMapping("/")
public class CajaRestController {
	@Autowired
	CajaService cajaService;
	@Autowired
	ValidacionService validacion;
	
	@Secured({"ROLE_ADMIN"})
	@GetMapping("get/cajas/to-arqueos")
	public List<Caja> obtenerCajasToArqueos() {
		return cajaService.obtenerTodasCajasDisponibles();
	}

	@Secured({"ROLE_ADMIN"})
	@GetMapping("get/cajas/{page}")
	public ResponseEntity<?> obtenerCajas(@PathVariable Integer page) {
		Map<String, Object> response = new HashMap<>();
		Page<Caja> lista;
		try {
			Pageable pageable = PageRequest.of(page, 10);
			lista = cajaService.listarCajasPaginado(pageable);
		} catch (DataAccessException e) {
			response.put("mensaje", "lista no obtenida");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Lista obtenida");
		response.put("cajas", lista);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@Secured({"ROLE_ADMIN"})
	@PostMapping("register/new/caja")
	public ResponseEntity<?> registrarCaja(@RequestBody Caja caja) {
		Map<String, Object> response = new HashMap<>();
		List<String> errores = validacion.camposCaja(caja);
		if (errores.size() != 0) {
			response.put("mensaje", "Los campos tienen errores.");
			response.put("errores", "Campos inválidos");
			response.put("errores", errores);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}
		if (caja.getId() == null) {
			caja.setEliminated(false);
			caja.setEstado(true);
		}
		try {
			cajaService.registrarCaja(caja);
		} catch (DataAccessException e) {
			response.put("mensaje", "Caja no registrada.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Caja registrada correctamente.");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@Secured({"ROLE_ADMIN"})
	@DeleteMapping("delete/caja/{id}")
	public ResponseEntity<?> eliminarLogicamenteCaja(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			Caja caja = cajaService.obtenerCajaId(id);
			cajaService.eliminarLogicamenteCaja(caja);
		} catch (DataAccessException e) {
			response.put("mensaje", "Caja no eliminada.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Caja eliminada.");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
