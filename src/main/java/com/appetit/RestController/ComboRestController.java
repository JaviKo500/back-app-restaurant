package com.appetit.RestController;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appetit.models.Categoria;
import com.appetit.models.Combo;
import com.appetit.service.CategoriaService;
import com.appetit.service.ComboService;
import com.appetit.service.ValidacionService;

@RestController
@CrossOrigin("*")
@RequestMapping("/")
public class ComboRestController {

	@Autowired
	ComboService comboService;

	@Autowired
	ValidacionService validaciones;

	@Autowired
	CategoriaService categoriaService;

	@Secured({ "ROLE_ADMIN" })
	@GetMapping("combos/cargar/{term}")
	public ResponseEntity<?> filtrarCombosByTermino(@PathVariable String term) {
		Map<String, Object> response = new HashMap<>();
		List<Combo> lista = new ArrayList<>();

		try {
			lista = comboService.filtrarCombosNombre(term);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al obtener la lista de combos.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "lista de combos");
		response.put("combos", lista);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@Secured({ "ROLE_ADMIN" })
	@PutMapping("actualizar/estado/combo")
	public ResponseEntity<?> CambiarEstadoProducto(@RequestBody Combo combo) {
		Map<String, Object> response = new HashMap<>();

		try {
			comboService.registrarCombo(combo);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al cambiar el estado del combo.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Se actualizó el estado");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@Secured({ "ROLE_ADMIN" })
	@GetMapping("get/combo/{id}")
	public ResponseEntity<?> GetCategorias(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		Combo combo = null;

		try {
			combo = comboService.buscarbyId(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al solicitar combo por id.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (combo == null) {
			response.put("mensaje", "No existe el combo solicitado.");
			response.put("error", "error de id de combo.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_GATEWAY);
		}

		response.put("mensaje", "combo obtenido.");
		response.put("combo", combo);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	// combos de la vista del cliente
	@GetMapping("get/categories/combos")
	public ResponseEntity<?> GetCategorias() {
		Map<String, Object> response = new HashMap<>();
		List<Categoria> categorias;
		try {
			categorias = categoriaService.findCategoriasCombos();
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al mapear las categorías.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (categorias.size() == 0) {
			response.put("mensaje", "No existen categorías en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		response.put("mensaje", "lista de categorías obtenida");
		response.put("categorias", categorias);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@GetMapping("get/client/combos/{id_cate}")
	public ResponseEntity<?> obtenerCombos(@PathVariable Long id_cate) {
		Map<String, Object> response = new HashMap<>();
		Categoria categoria = null;
		List<Combo> lista;

		try {
			categoria = categoriaService.BuscarCategoriaById(id_cate);
			lista = comboService.obtenerCombosCliente(categoria);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al obtener los combos.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "listado de combos");
		response.put("combos", lista);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@Secured({ "ROLE_ADMIN" })
	@GetMapping("get/combos-disponibles/{page}")
	public ResponseEntity<?> obtenerCombos(@PathVariable Integer page) {
		Map<String, Object> response = new HashMap<>();
		Page<Combo> listaCombos;

		try {
			Pageable pageable = PageRequest.of(page, 10);
			listaCombos = comboService.obtenerCombosPage(pageable);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al obtener la lista de Combos.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "lista obtenida");
		response.put("combos", listaCombos);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@Secured({ "ROLE_ADMIN" })
	@PostMapping("registrar/combo")
	public ResponseEntity<?> registrarCombo(@RequestBody Combo combo) {
		Map<String, Object> response = new HashMap<>();
		Combo com = null;
		if (combo == null) {
			response.put("mensaje", "Los campos estan incompletos");
			response.put("error", "No existen los campos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_GATEWAY);
		}
		List<String> errores = validaciones.camposCombo(combo);
		if (errores.size() != 0) {
			response.put("mensaje", "Existen errores.");
			response.put("errores", errores);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}

		try {
			combo.setEliminado(false);
			com = comboService.registrarCombo(combo);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al registrar un combo.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Combo registrado.");
		response.put("id_combo", com.getId());
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

}
