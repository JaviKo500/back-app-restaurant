package com.appetit.RestController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.appetit.configuration.RutaImagenes;
import com.appetit.imagenes.IUploadFileService;
import com.appetit.models.Categoria;
import com.appetit.models.TipoCategoria;
import com.appetit.service.CategoriaService;

@RestController
@CrossOrigin("*")
@RequestMapping("/")
public class CategoriaRestController {

	@Autowired
	CategoriaService categoriaService;

	@Autowired
	IUploadFileService fileService;

	@GetMapping("get/tipo-categorias")
	public List<TipoCategoria> getTiposCategorias() {
		return categoriaService.getAllTiposCategoria();
	}

	@GetMapping("get/categories")
	public ResponseEntity<?> GetAllCategorias() {
		Map<String, Object> response = new HashMap<>();
		List<Categoria> categorias;
		try {
			categorias = categoriaService.AllCategories();
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al mapear las categorías");
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

	//productos clientes
	@GetMapping("get/categories/products")
	public ResponseEntity<?> GetCategorias() {
		Map<String, Object> response = new HashMap<>();
		List<Categoria> categorias;
		try {
			categorias = categoriaService.findCategoriasProductos();
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al mapear las categorías");
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

	@Secured({ "ROLE_ADMIN" })
	@GetMapping("get/categories/{page}")
	public ResponseEntity<?> GetCategoriasPage(@PathVariable Integer page) {
		Map<String, Object> response = new HashMap<>();
		Page<Categoria> categorias;
		try {
			Pageable pageable = PageRequest.of(page, 10);
			categorias = categoriaService.AllCategoriesPageable(pageable);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al mapear las categorías");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "lista de categorías obtenida");
		response.put("categorias", categorias);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@Secured({ "ROLE_ADMIN" })
	@PostMapping("register/category")
	public ResponseEntity<?> RegistarCategoria(@RequestBody Categoria categoria) {
		Map<String, Object> response = new HashMap<>();
		Categoria cat = null;
		if (categoria == null || categoria.getNombre().length() < 2) {
			response.put("mensaje", "Los datos a registrar son erroneos.");
			response.put("error", "Complete los campos obligatorios.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		try {
			cat = categoriaService.BuscarrCategoriaNombre(categoria.getNombre());
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al registar una nueva categoria.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (cat != null) {
			response.put("mensaje", "Error al registar una nueva categoría.");
			response.put("error", "Yá existe una categoría con ese nombre.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		try {
			categoriaService.RegisterCategoria(categoria);
		} catch (DataAccessException e) {
			// capturar los errores posibles para reportar al cliente
			response.put("mensaje", "Error al registar una nueva categoría.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Categoria " + categoria.getNombre() + " registrada correctamente.");
		response.put("id", categoria.getId());
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@Secured({ "ROLE_ADMIN" })
	@PutMapping("update/category/{id}")
	public ResponseEntity<?> ActualizarCategoria(@RequestBody Categoria categoria, @PathVariable long id) {
		Map<String, Object> response = new HashMap<>();
		Categoria catExistente = null;
		Categoria catNom = null; /// objeto para solicitar categoria por nombre y comprobar si ya existe
		if (categoria == null) {
			response.put("mensaje", "Los datos a actualizar son erroneos.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		try {
			catNom = categoriaService.BuscarrCategoriaNombre(categoria.getNombre());
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar la categoría.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (catNom != null && catNom.getId() != categoria.getId()) {
			response.put("mensaje", "Error al actualizar la categoría.");
			response.put("error", "Ya existe una categoría con ese nombre");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_GATEWAY);
		}

		catExistente = categoriaService.BuscarCategoriaById(id);

		if (catExistente == null) {
			response.put("mensaje", "La categoria solicitada no existe en la base de datos.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			catExistente.setEstado(categoria.getEstado());
			catExistente.setNombre(categoria.getNombre());
			categoriaService.RegisterCategoria(catExistente);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar la categoria: " + categoria.getNombre());
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Categoria " + categoria.getNombre() + " actualizáda correctamente.");
		response.put("id", categoria.getId());
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	// metodo de carga de imagenes
	@Secured({ "ROLE_ADMIN" })
	@PostMapping("register/category/image/upload")
	public ResponseEntity<?> imgProducto(@RequestParam("archivo") MultipartFile archivo, @RequestParam("id") Long id) {

		Map<String, Object> response = new HashMap<String, Object>();
		Categoria categoria = null;
		try {
			categoria = categoriaService.BuscarCategoriaById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al obtener la categoría en la base de datos");
			response.put("error", e.getMessage().concat(": ".concat(e.getMostSpecificCause().getMessage())));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (categoria == null) {
			response.put("mensaje", "No existe la categoría solicitada");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		if (!archivo.isEmpty()) {

			String nombreArchivo = null;

			try {
				nombreArchivo = fileService.copiar(archivo, RutaImagenes.RUTA_CATEGORIAS);
			} catch (IOException e) {
				response.put("mensaje", "Error al subir la imagen de categoría.");
				response.put("error", e.getCause().getMessage());
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}

			String nombreFotoAnterior = categoria.getImagen();
			fileService.eliminar(nombreFotoAnterior, RutaImagenes.RUTA_CATEGORIAS);
			categoria.setImagen(nombreArchivo);
			categoriaService.RegisterCategoria(categoria);

			response.put("mensaje", "¡Imagen creada correctamente!");
			response.put("categoria", categoria);
		}
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);

	}

	@GetMapping("category/img/{nombreImg:.+}") // :.+ es una expresion reguar de que es un archivo
	public ResponseEntity<Resource> GetimagenProd(@PathVariable String nombreImg) {

		Resource recurso = null;

		try {
			recurso = fileService.cargar(nombreImg, RutaImagenes.RUTA_CATEGORIAS);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		HttpHeaders cabecera = new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"");

		return new ResponseEntity<Resource>(recurso, cabecera, HttpStatus.OK);

	}

	@Secured({ "ROLE_ADMIN" })
	@DeleteMapping("delete/category/{id}")
	public ResponseEntity<?> deleteCategoria(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		Categoria categoria = categoriaService.BuscarCategoriaById(id);
		String error = "";
		String nombreImagen = categoria.getImagen();
		try {
			categoriaService.deleteCategoriabyId(categoria);
		} catch (DataAccessException e) {
			response.put("error", e.getMostSpecificCause().getMessage());
			response.put("mensaje", "Error al eliminar el producto");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		try {
			fileService.eliminar(nombreImagen, RutaImagenes.RUTA_CATEGORIAS);
		} catch (Exception e) {
			error = "Error al eliminar la imagen del producto";
		}
		response.put("error", error);
		response.put("mensaje", "Categoría eliminada");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@Secured({ "ROLE_ADMIN" })
	@DeleteMapping("delete/category/definitive/{id}")
	public ResponseEntity<?> deleteCategoriaDefinitive(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		Categoria categoria = categoriaService.BuscarCategoriaById(id);
		String error = "";
		String nombreImagen = categoria.getImagen();
		try {
			categoriaService.deleteCategoriaDefinitiveById(id);
		} catch (DataAccessException e) {
			response.put("error", e.getMostSpecificCause().getMessage());
			response.put("mensaje", "Error al eliminar el producto");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		try {
			fileService.eliminar(nombreImagen, RutaImagenes.RUTA_CATEGORIAS);
		} catch (Exception e) {
			error = "Error al eliminar la imagen del producto";
		}
		response.put("error", error);
		response.put("mensaje", "Categoría eliminada");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

}
