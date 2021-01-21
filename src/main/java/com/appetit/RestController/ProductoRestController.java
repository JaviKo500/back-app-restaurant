package com.appetit.RestController;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.appetit.models.Producto;
import com.appetit.service.ProductoService;

@Controller
@CrossOrigin("*")
@RequestMapping("/")
public class ProductoRestController {

	@Autowired
	ProductoService productoService;

	@GetMapping("get/products")
	public ResponseEntity<?> ListaDeProductos() {
		Map<String, Object> response = new HashMap<>();
		List<Producto> lista = new ArrayList<>();

		try {
			lista = productoService.ObtenerProductos();
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al obtener la lista de productos.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "lista de productos");
		response.put("productos", lista);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@GetMapping("get/product/{id}")
	public ResponseEntity<?> ProductoByid(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		Producto prod = null;

		try {
			prod = productoService.BuscarProductoById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al obtener el producto");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (prod == null) {
			response.put("mensaje", "El producto solicitado no existe");
			response.put("error", "Id de producto erróneo");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "producto obtenido");
		response.put("producto", prod);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@GetMapping("get/products/{page}")
	public ResponseEntity<?> ListaDeProductosPage(@PathVariable Integer page) {
		Map<String, Object> response = new HashMap<>();
		Page<Producto> lista;

		try {
			Pageable pageable = PageRequest.of(page, 10);
			lista = productoService.ObtenerProductosPage(pageable);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al obtener la lista de productos.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "lista de productos");
		response.put("productos", lista);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@PostMapping("register/product")
	public ResponseEntity<?> RegistrarProducto(@RequestBody Producto producto) {
		Map<String, Object> response = new HashMap<>();
		Producto prod = null;
		if (producto == null) {
			response.put("mensaje", "Los datos a registrar son erroneos.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		// procso para guardar el producto
		try {
			prod = productoService.RegistrarProducto(producto);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al registar el producto.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Producto guardado correctamente");
		response.put("id", prod.getId());
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
		System.out.println(id + " entro aqui");
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

	@GetMapping("product/img/{nombreImg:.+}") // :.+ es una expresion reguar de que es un archivo
	public ResponseEntity<Resource> GetimagenProd(@PathVariable String nombreImg) {

		Path rutaArchivo = Paths.get(RutaImagenes.RUTA_PRODUCTOS).resolve(nombreImg).toAbsolutePath();
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

	@DeleteMapping("delete/product/{id}")
	public ResponseEntity<?> deleteCategoria(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		Producto prod = productoService.BuscarProductoById(id);
		String error = "";
		try {
			productoService.DeleteProductoById(id);
		} catch (DataAccessException e) {
			response.put("error", e.getMostSpecificCause().getMessage());
			response.put("mensaje", "Error al eliminar el producto");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		try {
			if (prod.getImagen() != null && prod.getImagen().length() > 0) {
				Path rutaImg = Paths.get(RutaImagenes.RUTA_PRODUCTOS).resolve(prod.getImagen()).toAbsolutePath();
				File archivoImg = rutaImg.toFile();
				if (archivoImg.exists() && archivoImg.canRead()) {
					archivoImg.delete();
				}
			}
		} catch (Exception e) {
			error = "Error al eliminar la imagen del producto";
		}
		response.put("error", error);
		response.put("mensaje", "Producto eliminado");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

}
