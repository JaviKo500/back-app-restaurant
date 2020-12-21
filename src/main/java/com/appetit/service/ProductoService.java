package com.appetit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appetit.models.Producto;
import com.appetit.repository.IProductoRepo;

@Service
public class ProductoService {

	@Autowired
	IProductoRepo productoRepo;

	public Producto RegistrarProducto(Producto producto) {
		return productoRepo.save(producto);
	}

	public Producto BuscarProductoById(Long id) {
		return productoRepo.findById(id).orElse(null);
	}

}
