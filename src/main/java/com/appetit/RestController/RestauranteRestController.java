package com.appetit.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appetit.models.Restaurante;
import com.appetit.service.RestauranteService;
import com.appetit.service.ValidacionService;

@RestController
@CrossOrigin("*")
@RequestMapping("/")
public class RestauranteRestController {

	@Autowired
	RestauranteService restauranteService;
	@Autowired
	ValidacionService validaciones;

	@Secured({ "ROLE_ADMIN" })
	@GetMapping("get/restautant/data")
	public ResponseEntity<?> obtenerDataRestaurante() {
		Map<String, Object> response = new HashMap<>();
		Restaurante restaurant;
		try {
			restaurant = restauranteService.obtenerDatosRestaurante(1L);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al obtener los datos del restaurante.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Correcto");
		response.put("restaurante", restaurant);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@Secured({ "ROLE_ADMIN" })
	@PostMapping("register/restaurant/data")
	public ResponseEntity<?> RegistrarRestaurante(@RequestBody Restaurante restaurante) {
		Map<String, Object> response = new HashMap<>();
		List<String> errores = validaciones.camposConfiguracion(restaurante);
		if (errores.size() != 0) {
			response.put("errores", errores);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}

		if (!restaurante.getCelular().startsWith("0")) {
			restaurante.setCelular("0" + restaurante.getCelular());
		}
		if (!restaurante.getTelefono().startsWith("0")) {
			restaurante.setTelefono("0" + restaurante.getTelefono());
		}
		
		try {
			// guardar datos del restaurante
			restauranteService.SaveDatosRestaurante(restaurante);
		} catch (DataAccessException e) {
			// capturar los errores que se puedan presentar al registrar un nuevo
			// restaurante;
			response.put("mensaje", "Error al registrar un nuevo restaurante.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

}
