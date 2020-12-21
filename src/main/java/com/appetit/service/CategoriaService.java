package com.appetit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appetit.models.Categoria;
import com.appetit.repository.ICategoriaRepo;

@Service
public class CategoriaService {

	@Autowired
	ICategoriaRepo categoriaRepo;

	public List<Categoria> AllCategories() {
		return categoriaRepo.findAll();
	}

	public Categoria RegisterCategoria(Categoria categoria) {
		return categoriaRepo.save(categoria);
	}

	public Categoria BuscarCategoriaById(Long id) {
		return categoriaRepo.findById(id).orElse(null);
	}

}
