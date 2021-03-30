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

import com.appetit.models.Role;
import com.appetit.models.Sexo;
import com.appetit.models.Usuario;
import com.appetit.service.UsuarioService;
import com.appetit.service.ValidacionService;

@RestController
@CrossOrigin("*")
@RequestMapping("/")
public class UsuarioRestController {

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private ValidacionService validacionService;

	// lista de usuarios para el arqueo
	@Secured("ROLE_ADMIN")
	@GetMapping("get/usuarios/to-arqueos")
	public List<Usuario> obtenerusuariosParaArqueos() {
		return usuarioService.ObtenerListaUsuariosArqueos();
	}

	// lista de roles existentes
	@Secured("ROLE_ADMIN")
	@GetMapping("get/user/roles")
	public ResponseEntity<?> obtenerRoles() {
		Map<String, Object> response = new HashMap<>();
		List<Role> lista;
		try {
			lista = usuarioService.obtenerRoles();
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al obtener la lista de Roles.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Lista de roles");
		response.put("roles", lista);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	// lista de sexos existentes
	@Secured("ROLE_ADMIN")
	@GetMapping("get/user/sexos")
	public ResponseEntity<?> obtenerSexos() {
		Map<String, Object> response = new HashMap<>();
		List<Sexo> lista;
		try {
			lista = usuarioService.obtenerlistaSexos();
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al obtener la lista de Sexos.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Lista de sexos");
		response.put("sexos", lista);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	// obtener usuario por id
	@Secured("ROLE_ADMIN")
	@GetMapping("get/user/{id}")
	public ResponseEntity<?> obtenerUsuarioId(@PathVariable long id) {
		Map<String, Object> response = new HashMap<>();
		Usuario user = null;
		try {
			user = usuarioService.buscarusuarioById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al obtener el usuario");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (user == null) {
			response.put("mensaje", "No existe el usuario en la base de datos.");
			response.put("error", "El id de usuario no coinside.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		response.put("mensaje", "Usuario encontrado");
		response.put("usuario", user);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	// obtener usuario por id
	@Secured("ROLE_ADMIN")
	@GetMapping("get/user/movimiento/{id}")
	public ResponseEntity<?> obtenerUsuarioIdMovimiento(@PathVariable long id) {
		Map<String, Object> response = new HashMap<>();
		Usuario user = null;
		try {
			user = usuarioService.buscarusuarioByIdMovimiento(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al obtener el usuario");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (user == null) {
			response.put("mensaje", "No existe el usuario en la base de datos.");
			response.put("error", "El id de usuario no coinside.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		response.put("mensaje", "Usuario encontrado");
		response.put("usuario", user);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@Secured("ROLE_ADMIN")
	@GetMapping("get/users/{page}")
	public ResponseEntity<?> registrarUsuario(@PathVariable Integer page) {
		Map<String, Object> response = new HashMap<>();
		Page<Usuario> usuarios;
		// paginacion de usuarios
		try {
			Pageable pageable = PageRequest.of(page, 10);
			usuarios = usuarioService.listarUsuariosPage(pageable);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al obtener la lista de Usuarios.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// responde la lista de usuarios
		response.put("mensaje", "lista de usuarios");
		response.put("usuarios", usuarios);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@Secured("ROLE_ADMIN")
	@PostMapping("register/user")
	public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
		Map<String, Object> response = new HashMap<>();

		if (usuario == null) {
			response.put("mensaje", "El usuario no contiene todos los campos correctos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		} else {
			usuario.setTelefono("0" + usuario.getTelefono());
			usuario.setCedula("0" + usuario.getCedula());
		}
		// asignar estado true al usuario
		if (usuario.getId() == null) {
			usuario.setEstado(true);
			usuario.setEliminated(false);
		}

		// validar campos
		List<String> errores = validacionService.camposUsuario(usuario);
		if (errores.size() != 0) {
			response.put("mensaje", errores);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}

		// registrar usuario
		try {
			usuarioService.registrarUsuario(usuario);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al registrar el Usuario.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Usuario registrado correctamente.");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@Secured("ROLE_ADMIN")
	@PutMapping("update/user")
	public ResponseEntity<?> actualizarUsuario(@RequestBody Usuario usuario) {
		Map<String, Object> response = new HashMap<>();
		Usuario usuActual = null;
		// validar que sea el mismo usuario
		if (usuario == null || usuario.getId() == null) {
			response.put("mensaje", "El usuario no contiene todos los campos correctos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		} else {
			usuario.setTelefono("0" + usuario.getTelefono());
			usuario.setCedula("0" + usuario.getCedula());
		}
		// validar campos
		List<String> errores = validacionService.camposUsuario(usuario);
		if (errores.size() != 0) {
			response.put("mensaje", errores);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}
		// buscar usuario por id
		try {
			usuActual = usuarioService.buscarusuarioById(usuario.getId());
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al obtener el Usuario para actualizar.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// validar si existe el usuario
		if (usuActual == null) {
			response.put("mensaje", "El usuario solicitado no existe en la base de datos");
			response.put("error", "Error de id");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		// actualizar los datos
		try {
			usuActual.setApellido(usuario.getApellido());
			usuActual.setCedula(usuario.getCedula());
			usuActual.setEmail(usuario.getEmail());
			usuActual.setNombre(usuario.getNombre());
			usuActual.setPassword(usuario.getPassword());
			usuActual.setRoles(usuario.getRoles());
			usuActual.setTelefono(usuario.getTelefono());
			usuActual.setUsername(usuario.getUsername());
			usuActual.setEstado(usuario.getEstado());
			usuActual.setSexo(usuario.getSexo());
			usuarioService.registrarUsuario(usuActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar el Usuario.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Usuario actualizado correctamente");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);

	}

	@Secured("ROLE_ADMIN")
	@PutMapping("update/user/estado")
	public ResponseEntity<?> actualizarEstadoUsuario(@RequestBody Usuario usuario) {
		Map<String, Object> response = new HashMap<>();
		Usuario user = null;
		if (usuario == null) {
			response.put("mensaje", "Error en los datos del usuario");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		try {
			user = usuarioService.buscarusuarioById(usuario.getId());
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al obtener el Usuario para actualizar su estado.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (user == null) {
			response.put("mensaje", "El usuario solicitado no existe en la base de datos");
			response.put("error", "Error de id");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		if (user.getEstado() == true) {
			user.setEstado(false);
		} else {
			user.setEstado(true);
		}
		try {
			usuarioService.registrarUsuario(user);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar el estado del usuario.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@Secured("ROLE_ADMIN")
	@DeleteMapping("delete/user/{id}")
	public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		Usuario user = usuarioService.buscarusuarioById(id);
		if (user == null) {
			response.put("mensaje", "Error el id de usuario");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		try {
			usuarioService.EliminarLogicamenteUsuario(user);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al intentar eliminar un usuario.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Usuario eliminado correctaente.");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

}
