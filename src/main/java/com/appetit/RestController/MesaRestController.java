package com.appetit.RestController;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.appetit.models.Mesa;
import com.appetit.service.MesaService;

@Controller
@CrossOrigin("*")
@RequestMapping("/")
public class MesaRestController {

	@Autowired
	MesaService mesaService;

	@PostMapping("register/mesa")
	public ResponseEntity<?> RegistrarMesa(@RequestBody Mesa mesa) {
		Map<String, Object> response = new HashMap<>();
		Mesa mes = null;
		if (mesa == null) {
			response.put("mensaje", "Los datos a registrar son erroneos.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		// procso para guardar el producto
		try {
			mes = mesaService.RegistrarMesa(mesa);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al registar la mesa.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Mesa guardado correctamente");
		response.put("id", mes.getId());
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@GetMapping("get/mesa/{page}")
	public ResponseEntity<?> ListaDeMesasPage(@PathVariable Integer page) {
		Map<String, Object> response = new HashMap<>();
		Page<Mesa> lista;

		try {
			Pageable pageable = PageRequest.of(page, 10);
			lista = mesaService.ObtenerMesaPage(pageable);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al obtener la lista de Mesas.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "lista de mesas");
		response.put("mesa", lista);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@PutMapping("update/mesa/{id}")
	public ResponseEntity<?> UpdateDeMesas(@RequestBody Mesa mesa, @PathVariable long id) {
		Map<String, Object> response = new HashMap<>();

		Mesa mesActual = null;
		if (mesa == null) {
			response.put("mensaje", "Los datos a actualizar son erroneos.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		mesActual = mesaService.getMesaById(id);
		if (mesActual == null) {
			response.put("mensaje", "No existe la mesa solicitada");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			mesActual.setEstado(mesa.getEstado());
			mesActual.setNombre(mesa.getNombre());
			mesaService.RegistrarMesa(mesActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar la Mesa.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Mesa actualizada.");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@DeleteMapping("delete/mesa/{id}")
	public ResponseEntity<?> DeleteMesa(@PathVariable long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			mesaService.BorrarMesa(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar la Mesa.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Mesa eliminada correctamente");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

}