package com.appetit.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.appetit.configuration.RutaImagenes;
import com.appetit.models.Producto;
import com.appetit.service.ProductoService;

@Controller
@CrossOrigin("*")
@RequestMapping("/")
public class ProductoRestController {

	@Autowired
	ProductoService productoService;

	@PostMapping("register/product")
	public ResponseEntity<?> RegistrarProducto(@RequestBody Producto producto) {
		Map<String, Object> response = new HashMap<>();

		if (producto == null) {
			response.put("mensaje", "Los datos a registrar son erroneos.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			productoService.RegistrarProducto(producto);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al registar el producto.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Producto guardado correctamente");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@PutMapping("update/product/{id}")
	public ResponseEntity<?> ActualizarProducto(@RequestBody Producto producto, @PathVariable long id) {
		Map<String, Object> response = new HashMap<>();
		Producto prodActual = null;
		if (producto == null) {
			response.put("mensaje", "Los datos a actualizar son erroneos.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		prodActual = productoService.BuscarProductoById(producto.getId());
		if (prodActual == null) {
			response.put("mensaje", "No existe el producto solicitado");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		try {
			prodActual.setCategoria(producto.getCategoria());
			prodActual.setDescripcion(producto.getDescripcion());
			prodActual.setEstado(producto.getEstado());
			prodActual.setNombre(producto.getNombre());
			prodActual.setPrecio(producto.getPrecio());
			productoService.RegistrarProducto(prodActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar el producto.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Producto registrado correctamente");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	// carga de imagenes

	@PostMapping("register/product/img/upload")
	public ResponseEntity<?> imgProducto(@RequestParam("archivo") MultipartFile archivo, @RequestParam("id") Long id) {
		System.out.println(id);
		Map<String, Object> response = new HashMap<String, Object>();
		Producto producto = null;
		try {
			producto = productoService.BuscarProductoById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al obtener el producto en la base de datos");
			response.put("error", e.getMessage().concat(": ".concat(e.getMostSpecificCause().getMessage())));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (producto == null) {
			response.put("mensaje", "No existe el producto solicitado");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		if (!archivo.isEmpty()) {
			// uuid genera und id random unico
			String nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename().replace(" ", "");
			Path rutaArchivo = Paths.get(RutaImagenes.RUTA_PRODUCTOS).resolve(nombreArchivo).toAbsolutePath();// crea la
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
			String NomImgAnterior = producto.getImagen();
			if (NomImgAnterior != null && NomImgAnterior.length() > 0) {
				Path rutaImgAnterior = Paths.get(RutaImagenes.RUTA_PRODUCTOS).resolve(NomImgAnterior).toAbsolutePath();
				File archivoImgAnterior = rutaImgAnterior.toFile();
				if (archivoImgAnterior.exists() && archivoImgAnterior.canRead()) {
					archivoImgAnterior.delete();
				}
			}

			producto.setImagen(nombreArchivo);
			productoService.RegistrarProducto(producto);
			response.put("mensaje", "¡Imagen creada correctamente!");
		} else {
			response.put("mensaje", "¡No se econtro una imagen para asignar!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		response.put("producto", producto);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

}
