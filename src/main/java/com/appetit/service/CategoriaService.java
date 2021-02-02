package com.appetit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.appetit.models.Categoria;
import com.appetit.repository.ICategoriaRepo;

@Service
public class CategoriaService {

	@Autowired
	ICategoriaRepo categoriaRepo;

	@Transactional(readOnly = true)
	public List<Categoria> AllCategories() {
		return categoriaRepo.findAll();
	}

	@Transactional(readOnly = true)
	public Categoria BuscarrCategoriaNombre(String nombre) {
		return categoriaRepo.findByNombre(nombre);
	}

	@Transactional(readOnly = true)
	public Page<Categoria> AllCategoriesPageable(Pageable pageable) {
		return categoriaRepo.findAll(pageable);
	}

	@Transactional
	public Categoria RegisterCategoria(Categoria categoria) {
		return categoriaRepo.save(categoria);
	}

	@Transactional(readOnly = true)
	public Categoria BuscarCategoriaById(Long id) {
		return categoriaRepo.findById(id).orElse(null);
	}

	@Transactional
	public void deleteCategoriabyId(Long id) {
		categoriaRepo.deleteById(id);
	}

}
