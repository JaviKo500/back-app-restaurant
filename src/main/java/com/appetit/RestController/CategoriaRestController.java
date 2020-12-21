package com.appetit.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.appetit.configuration.RutaImagenes;
import com.appetit.models.Categoria;
import com.appetit.service.CategoriaService;

@Controller
@CrossOrigin("*")
@RequestMapping("/")
public class CategoriaRestController {

	@Autowired
	CategoriaService categoriaService;

	@GetMapping("get/categories")
	public ResponseEntity<?> GetCategorias() {
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

	@PostMapping("register/categoria")
	public ResponseEntity<?> RegistarCategoria(@RequestBody Categoria categoria) {
		Map<String, Object> response = new HashMap<>();
		if (categoria == null || categoria.getNombre().length() < 2) {
			response.put("mensaje", "Los datos a registrar son erroneos.");
			response.put("error", "Complete los campos obligatorios.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		try {
			categoriaService.RegisterCategoria(categoria);
		} catch (DataAccessException e) {
			// capturar los errores posibles para reportar al cliente
			response.put("mensaje", "Error al registar una nueva categoria.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Categoria " + categoria.getNombre() + " registrada correctamente.");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@PutMapping("update/categoria/{id}")
	public ResponseEntity<?> ActualizarCategoria(@RequestBody Categoria categoria, @PathVariable long id) {
		Map<String, Object> response = new HashMap<>();
		Categoria catExistente = null;
		if (categoria == null) {
			response.put("mensaje", "Los datos a actualizar son erroneos.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
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
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	// metodo de carga de imagenes

	@PostMapping("register/category/image/upload")
	public ResponseEntity<?> imgProducto(@RequestParam("archivo") MultipartFile archivo, @RequestParam("id") Long id) {
		System.out.println(id);
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
			// uuid genera und id random unico
			String nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename().replace(" ", "");
			Path rutaArchivo = Paths.get(RutaImagenes.RUTA_CATEGORIAS).resolve(nombreArchivo).toAbsolutePath();// crea
																												// la
																												// ruta
			// con el
			// nombre
			try {
				Files.copy(archivo.getInputStream(), rutaArchivo);
			} catch (IOException e) {
				e.printStackTrace();
				response.put("mensaje", "¡Error al guardar la imagen en la base de datos!");
				response.put("error", e.getMessage().concat(": ").concat(e.getCause().getMessage()));
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			// borrar foto anterior --------- si se borra un producto hay que llamar este
			// metodo
			String NomImgAnterior = categoria.getImagen();
			if (NomImgAnterior != null && NomImgAnterior.length() > 0) {
				Path rutaImgAnterior = Paths.get(RutaImagenes.RUTA_CATEGORIAS).resolve(NomImgAnterior).toAbsolutePath();
				File archivoImgAnterior = rutaImgAnterior.toFile();
				if (archivoImgAnterior.exists() && archivoImgAnterior.canRead()) {
					archivoImgAnterior.delete();
				}
			}

			categoria.setImagen(nombreArchivo);
			categoriaService.RegisterCategoria(categoria);
			response.put("mensaje", "¡Imagen creada correctamente!");
		} else {
			response.put("mensaje", "¡No se econtro una imagen para asignar!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		response.put("categoria", categoria);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

}
