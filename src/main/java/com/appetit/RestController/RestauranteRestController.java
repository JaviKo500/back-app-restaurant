package com.appetit.RestController;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appetit.models.Restaurante;
import com.appetit.service.RestauranteService;

@RestController
@CrossOrigin("*")
@RequestMapping("/")
public class RestauranteRestController {

	@Autowired
	RestauranteService restauranteService;

	@PostMapping("register/restaurante/data")
	public ResponseEntity<?> RegistrarRestaurante(@RequestBody Restaurante restaurante) {
		Map<String, Object> response = new HashMap<>();

		try {
			// guardar datos del restaurante
			restauranteService.SaveDatosRestaurante(restaurante);
		} catch (DataAccessException e) {
			// capturar los errores que se puedan presentar al registrar un nuevo
			// restaurante;
			response.put("mensaje", "Error al registrar un nuevi restaurante.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

}
