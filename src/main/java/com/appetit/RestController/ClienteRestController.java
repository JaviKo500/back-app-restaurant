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

	// obtener listado de clientes
	@Secured({ "ROLE_ADMIN" })
	@GetMapping("get/clientes/page/{page}")
	public ResponseEntity<?> obtenerClientesPageable(@PathVariable Integer page) {
		Map<String, Object> response = new HashMap<>();
		Page<Cliente> clientes = null;
		try {
			Pageable pageable = PageRequest.of(page, 10);
			clientes = clienteService.listarClientesPaginado(pageable);
		} catch (DataAccessException e) {
			response.put("mensaje", "lista no obtenida");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Lista obtenida");
		response.put("clientes", clientes);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	// registro por parte del cliente
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
			if (cliente.getCelular().length() == 9) {
				cliente.setCelular("0" + cliente.getCelular());
			}

		}
		// en caso de registro nuevo asisgnar estado de falso al emiminado
		if (cliente.getId() == null) {
			cliente.setEliminated(false);
		}
		List<String> errores = validacionService.camposCliente(cliente);
		if (errores.size() != 0) {
			response.put("errores", errores);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}
		if (cliente.getId() == null) {
			clie = clienteService.FindClineteByCedula(cliente.getCedula());
			if (clie != null) {
				response.put("mensaje",
						"El cliente con la cédula: " + clie.getCedula() + " Ya existe en la base de datos.");
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
			}
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
	@Secured({ "ROLE_ADMIN" })
	@PutMapping("cliente/update/{id}")
	public ResponseEntity<?> ActualizarClienteid(@RequestBody Cliente cliente, @PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		Cliente clientRegis = null;

		if (validacionService.Email(cliente.getEmail()) == false) {
			response.put("mensaje", "Error de ingreso, cliente no actualizado.");
			response.put("error", "El email ingresado es inválido");
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
			response.put("mensaje", "Error, no se actualizó el cliente");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Cliente actualizado correctamente.");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@Secured({ "ROLE_ADMIN" })
	@GetMapping("cliente/findbyced/{cedula}")
	public ResponseEntity<?> ObtenerClienteCedula(@PathVariable String cedula) {
		Map<String, Object> response = new HashMap<>();
		Cliente client = null;
		if (cedula.length() < 10) {
			response.put("mensaje", "Ingrese un numero de cédula correcto.");
			response.put("error", "Cédula errónea");
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
			response.put("mensaje", "El cliente con la cédula: " + cedula + " no existe en la base de datos.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		response.put("mensaje", "Cliente obtenido");
		response.put("cliente", client);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	// obtener cliente por cedula
	@Secured({ "ROLE_ADMIN" })
	@GetMapping("cliente/find-by-id/{id}")
	public ResponseEntity<?> ObtenerClienteId(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		Cliente client = null;
		try {
			client = clienteService.findClienteByIDAndEliminated(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al intentar obtener un cliente.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (client == null) {
			response.put("mensaje", "El cliente no existe en la base de datos.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		response.put("mensaje", "Cliente obtenido");
		response.put("cliente", client);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@Secured({ "ROLE_ADMIN" })
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
			clienteService.deleteClienteLogicamente(clien);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al intentar eliminar un cliente.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El cliente fué eliminado con éxito");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
