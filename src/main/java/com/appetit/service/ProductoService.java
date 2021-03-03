package com.appetit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.appetit.models.Categoria;
import com.appetit.models.Producto;
import com.appetit.repository.IProductoRepo;

@Service
public class ProductoService {

	@Autowired
	IProductoRepo productoRepo;

	@Transactional(readOnly = true)
	public List<Producto> filtrarProductosNombre(String termino) {
		return productoRepo.findByEstadoAndEliminatedAndNombreContainingIgnoreCase(true, false, termino);
	}

	@Transactional(readOnly = true)
	public List<Producto> productosClienteEstado(Categoria cate) {
		return productoRepo.findByEstadoAndCategoriaAndEliminated(true, cate, false);
	}

	@Transactional(readOnly = true)
	public Page<Producto> ObtenerProductosPage(Pageable pageable) {
		return productoRepo.findByEliminated(pageable, false);
	}

	@Transactional
	public Producto RegistrarProducto(Producto producto) {
		producto.setEliminated(false);
		return productoRepo.save(producto);
	}

	@Transactional(readOnly = true)
	public Producto BuscarProductoById(Long id) {
		return productoRepo.findByIdAndEliminated(id, false);
	}

	@Transactional
	public void DeleteProductoById(Producto prod) {
		prod.setImagen("");
		prod.setEliminated(true);
		productoRepo.save(prod);
	}
	@Transactional
	public void DeleteDefinitiveById(Long id) {
		productoRepo.deleteById(id);
	}

}
