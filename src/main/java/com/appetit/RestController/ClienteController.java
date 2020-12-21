package com.appetit.RestController;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.appetit.models.Cliente;
import com.appetit.service.ClienteService;

@Controller
@CrossOrigin("*")
@RequestMapping("/")
public class ClienteController {

	@Autowired
	private ClienteService clienteService;

	@PostMapping("cliente/register")
	public ResponseEntity<?> guardarCliente(@RequestBody Cliente cliente) {
		Map<String, Object> response = new HashMap<>();

		if (cliente == null) {
			response.put("mensaje", "Los datos a registrar son erroneos.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		try {
			clienteService.RegisterCliente(cliente);
		} catch (DataAccessException e) {
			// TODO: capturar posibles errores
			response.put("mensaje", "Error al intentar registrar un cliente.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Cliente registrado.");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
}
