package com.appetit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.appetit.models.Producto;
import com.appetit.repository.IProductoRepo;

@Service
public class ProductoService {

	@Autowired
	IProductoRepo productoRepo;

	@Transactional(readOnly = true)
	public List<Producto> ObtenerProductos() {
		return productoRepo.findAll();
	}

	@Transactional(readOnly = true)
	public Page<Producto> ObtenerProductosPage(Pageable pageable) {
		return productoRepo.findAll(pageable);
	}

	@Transactional
	public Producto RegistrarProducto(Producto producto) {
		return productoRepo.save(producto);
	}

	@Transactional(readOnly = true)
	public Producto BuscarProductoById(Long id) {
		return productoRepo.findById(id).orElse(null);
	}

	@Transactional
	public void DeleteProductoById(Long id) {
		productoRepo.deleteById(id);
	}

}
