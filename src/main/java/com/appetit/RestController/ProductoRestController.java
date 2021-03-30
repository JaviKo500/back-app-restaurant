package com.appetit.RestController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
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
import com.appetit.models.Combo;
import com.appetit.models.Producto;
import com.appetit.service.CategoriaService;
import com.appetit.service.ComboService;
import com.appetit.service.ProductoService;

@RestController
@CrossOrigin("*")
@RequestMapping("/")
public class ProductoRestController {

	@Autowired
	ProductoService productoService;

	@Autowired
	CategoriaService categoriaService;

	@Autowired
	IUploadFileService fileService;

	@Autowired
	ComboService comboService;

	@Secured({ "ROLE_ADMIN" })
	@PutMapping("actualizar/estado/producto")
	public ResponseEntity<?> CambiarEstadoProducto(@RequestBody Producto producto) {
		Map<String, Object> response = new HashMap<>();

		try {
			productoService.RegistrarProducto(producto);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al cambiar el estado del producto.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Se actualizó el estado");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@Secured({ "ROLE_ADMIN" })
	@GetMapping("productos/cargar/{term}")
	public ResponseEntity<?> filtrarProductosByTermino(@PathVariable String term) {
		Map<String, Object> response = new HashMap<>();
		List<Producto> lista = new ArrayList<>();

		try {
			lista = productoService.filtrarProductosNombre(term);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al obtener la lista de productos.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "lista de productos");
		response.put("productos", lista);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@GetMapping("get/client/products/{cate_id}")
	public ResponseEntity<?> ListaDeProductos(@PathVariable Long cate_id) {
		Map<String, Object> response = new HashMap<>();
		List<Producto> lista = new ArrayList<>();

		try {
			Categoria cate = categoriaService.BuscarCategoriaById(cate_id);
			lista = productoService.productosClienteEstado(cate);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al obtener la lista de productos.");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "lista de productos");
		response.put("productos", lista);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@Secured({ "ROLE_ADMIN" })
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

	@Secured({ "ROLE_ADMIN" })
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

	@Secured({ "ROLE_ADMIN" })
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
			// posiblemente quitar
			producto.setEstado(true);
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

	@Secured({ "ROLE_ADMIN" })
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
		response.put("id_producto", id);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	// carga de imagenes
	@Secured({ "ROLE_ADMIN" })
	@PostMapping("register/product/img/upload")
	public ResponseEntity<?> imgProducto(@RequestParam("archivo") MultipartFile archivo, @RequestParam("id") Long id,
			@RequestParam("tipo") String tipo) {
		Map<String, Object> response = new HashMap<String, Object>();
		System.out.print(tipo);
		Producto producto = null;
		Combo combo = null;
		String nombreFotoAnterior = "";

		if (tipo.equals("producto")) {
			System.out.print("entro a prodcutp");
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
			nombreFotoAnterior = producto.getImagen();
		}
		// en caso de ser combo
		if (tipo.equals("combo")) {
			System.out.print("entro a combo" + id);
			try {
				combo = comboService.buscarbyId(id);
			} catch (DataAccessException e) {
				response.put("mensaje", "Error al obtener el combo en la base de datos");
				response.put("error", e.getMessage().concat(": ".concat(e.getMostSpecificCause().getMessage())));
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			if (combo == null) {
				response.put("mensaje", "No existe el combo solicitado");
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
			nombreFotoAnterior = combo.getImagen();
		}

		if (!archivo.isEmpty()) {

			String nombreArchivo = null;

			try {
				nombreArchivo = fileService.copiar(archivo, RutaImagenes.RUTA_PRODUCTOS);
			} catch (IOException e) {
				response.put("mensaje", "Error al subir la imágen del producto.");
				response.put("error", e.getCause().getMessage());
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}

			fileService.eliminar(nombreFotoAnterior, RutaImagenes.RUTA_PRODUCTOS);
			// en el caso de ser un producto
			if (producto != null) {
				producto.setImagen(nombreArchivo);
				productoService.RegistrarProducto(producto);
				response.put("producto", producto);
			}
			// en el caso de ser combo
			if (combo != null) {
				combo.setImagen(nombreArchivo);
				comboService.registrarCombo(combo);
				response.put("combo", combo);
			}
			response.put("mensaje", "¡Imagen creada correctamente!");
		}
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);

	}

	@GetMapping("product/img/{nombreImg:.+}") // :.+ es una expresion reguar de que es un archivo
	public ResponseEntity<Resource> GetimagenProd(@PathVariable String nombreImg) {

		Resource recurso = null;

		try {
			recurso = fileService.cargar(nombreImg, RutaImagenes.RUTA_PRODUCTOS);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		HttpHeaders cabecera = new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"");

		return new ResponseEntity<Resource>(recurso, cabecera, HttpStatus.OK);
	}

	@Secured({ "ROLE_ADMIN" })
	@DeleteMapping("delete/product/{id}")
	public ResponseEntity<?> deleteproducto(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		Producto prod = productoService.BuscarProductoById(id);
		String nombreImagen = prod.getImagen();
		String error = "";
		try {
			productoService.DeleteProductoById(prod);
		} catch (DataAccessException e) {
			response.put("error", e.getMostSpecificCause().getMessage());
			response.put("mensaje", "Error al eliminar el producto");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		try {

			fileService.eliminar(nombreImagen, RutaImagenes.RUTA_PRODUCTOS);
		} catch (Exception e) {
			error = "Error al eliminar la imagen del producto";
		}

		response.put("error", error);
		response.put("mensaje", "Producto eliminado");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@Secured({ "ROLE_ADMIN" })
	@DeleteMapping("delete/product/definitivo/{id}")
	public ResponseEntity<?> deleteProductoDefinitivo(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		Producto prod = productoService.BuscarProductoById(id);
		String nombreImagen = prod.getImagen();
		String error = "";
		try {
			productoService.DeleteDefinitiveById(id);
		} catch (DataAccessException e) {
			response.put("error", e.getMostSpecificCause().getMessage());
			response.put("mensaje", "Error al eliminar el producto");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		try {

			fileService.eliminar(nombreImagen, RutaImagenes.RUTA_PRODUCTOS);
		} catch (Exception e) {
			error = "Error al eliminar la imagen del producto";
		}
		response.put("error", error);
		response.put("mensaje", "Producto eliminado");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

}
