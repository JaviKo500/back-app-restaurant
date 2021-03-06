package com.appetit.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appetit.models.Cliente;
import com.appetit.service.ClienteService;
import com.appetit.service.ValidacionService;

@RestController
@CrossOrigin("*")
@RequestMapping("/")
public class ClienteRestController {

	@Autowired
	private ClienteService clienteService;
	@Autowired
	private ValidacionService validacionService;

	@PostMapping("client/register/client")
	public ResponseEntity<?> guardarCliente(@RequestBody Cliente cliente) {
		Map<String, Object> response = new HashMap<>();
		Cliente clie = null;

		if (cliente == null) {
			response.put("mensaje", "Los datos a registrar son erroneos.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		} else {
			if (cliente.getCedula().length() == 9) {
				cliente.setCedula("0" + cliente.getCedula());
			}

			cliente.setCelular("0" + cliente.getCelular());
		}
		List<String> errores = validacionService.camposCliente(cliente);
		if (errores.size() != 0) {
			response.put("mensaje", errores);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}

		clie = clienteService.FindClineteByCedula(cliente.getCedula());
		if (clie != null) {
			response.put("mensaje",
					"El cliente con la c??dula: " + clie.getCedula() + " Ya existe en la base de datos.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		try {
			clie = clienteService.RegisterCliente(cliente);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al intentar registrar un cliente.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("cliente", clie);
		response.put("mensaje", "Cliente registrado.");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	// metodo para actualizar un cliente
	@PutMapping("cliente/update/{id}")
	public ResponseEntity<?> ActualizarClienteid(@RequestBody Cliente cliente, @PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		Cliente clientRegis = null;

		if (validacionService.Email(cliente.getEmail()) == false) {
			response.put("mensaje", "Error de ingreso, cliente no actualizado.");
			response.put("error", "El email ingresado es inv??lido");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		try {
			clientRegis = clienteService.findClienteByID(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error en el id ingresado.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (clientRegis == null) {
			response.put("mensaje", "El cliente no existe para actualizar.");
			response.put("error", "Error id");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			// pasar los datos nuevos al cliente
			clientRegis.setApellidos(cliente.getApellidos());
			clientRegis.setCelular(cliente.getCelular());
			clientRegis.setDireccion(cliente.getDireccion());
			clientRegis.setEmail(cliente.getEmail());
			clientRegis.setNombres(cliente.getNombres());
			clientRegis = clienteService.RegisterCliente(clientRegis);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error, no se actualiz?? el cliente");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Cliente actualizado correctamente.");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@GetMapping("cliente/findbyced/{cedula}")
	public ResponseEntity<?> ObtenerClienteCedula(@PathVariable String cedula) {
		Map<String, Object> response = new HashMap<>();
		Cliente client = null;
		if (cedula.length() < 10) {
			response.put("mensaje", "Ingrese un numero de c??dula correcto.");
			response.put("error", "C??dula err??nea");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		try {
			client = clienteService.FindClineteByCedula(cedula);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al intentar obtener un cliente.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (client == null) {
			response.put("mensaje", "El cliente con la c??dula: " + cedula + " no existe en la base de datos.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		response.put("mensaje", "Cliente obtenido");
		response.put("cliente", client);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@DeleteMapping("cliente/delete/{id}")
	public ResponseEntity<?> EliminarClienteID(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		System.out.print("lledo el " + id);
		Cliente clien = null;
		try {
			clien = clienteService.findClienteByID(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Problema al encontrar el cliente.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (clien == null) {
			response.put("mensaje", "El cliente no existe en la base de datos.");
			response.put("error", "Error de id");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		try {
			clienteService.deleteClienteByID(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al intentar eliminar un cliente.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El cliente fue eliminado con ??xito");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
