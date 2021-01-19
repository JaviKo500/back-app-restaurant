package com.appetit.RestController;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
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

	@PostMapping("register/category")
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
		response.put("id", categoria.getId());
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@PutMapping("update/category/{id}")
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
		response.put("id", categoria.getId());
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
			// id genera und id random unico
			String nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename().replace(" ", "");
			Path rutaArchivo = Paths.get(RutaImagenes.RUTA_CATEGORIAS).resolve(nombreArchivo).toAbsolutePath();// crea
																												// la
																												// ruta
			// con el nombre
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

	@GetMapping("category/img/{nombreImg:.+}") // :.+ es una expresion reguar de que es un archivo
	public ResponseEntity<Resource> GetimagenProd(@PathVariable String nombreImg) {

		Path rutaArchivo = Paths.get(RutaImagenes.RUTA_CATEGORIAS).resolve(nombreImg).toAbsolutePath();
		Resource recurso = null;
		try {
			recurso = new UrlResource(rutaArchivo.toUri());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!recurso.exists() && recurso.isReadable()) {
			throw new RuntimeException("No se pudo cargar la imagen de la ruta solicitada");
		}

		// forzar la descarga de la imagen
		HttpHeaders cabecera = new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attaachment; filename=\"" + recurso.getFilename() + "\"");

		return new ResponseEntity<Resource>(recurso, cabecera, HttpStatus.OK);

	}

	@DeleteMapping("delete/category/{id}")
	public ResponseEntity<?> deleteCategoria(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		Categoria categoria = categoriaService.BuscarCategoriaById(id);
		String error = "";
		try {
			categoriaService.deleteCategoriabyId(id);
		} catch (DataAccessException e) {
			response.put("error", e.getMostSpecificCause().getMessage());
			response.put("mensaje", "Error al eliminar el producto");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		try {
			if (categoria.getImagen() != null && categoria.getImagen().length() > 0) {
				Path rutaImg = Paths.get(RutaImagenes.RUTA_CATEGORIAS).resolve(categoria.getImagen()).toAbsolutePath();
				File archivoImg = rutaImg.toFile();
				if (archivoImg.exists() && archivoImg.canRead()) {
					archivoImg.delete();
				}
			}
		} catch (Exception e) {
			error = "Error al eliminar la imagen del producto";
		}
		response.put("error", error);
		response.put("mensaje", "Categoría eliminada");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

}
