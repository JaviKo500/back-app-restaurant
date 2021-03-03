package com.appetit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.appetit.models.Categoria;
import com.appetit.models.TipoCategoria;
import com.appetit.repository.ICategoriaRepo;
import com.appetit.repository.ITipoCategoriaRepo;


@Service
public class CategoriaService {

	@Autowired
	ICategoriaRepo categoriaRepo;

	@Autowired
	ITipoCategoriaRepo tipoRepo;

	@Transactional(readOnly = true)
	public List<TipoCategoria> getAllTiposCategoria() {
		return tipoRepo.findAll();
	}

	@Transactional(readOnly = true)
	public List<Categoria> AllCategories() {
		return categoriaRepo.findByEliminated(false);
	}

	@Transactional(readOnly = true)
	public Categoria BuscarrCategoriaNombre(String nombre) {
		return categoriaRepo.findByNombreAndEliminated(nombre, false);
	}

	@Transactional(readOnly = true)
	public Page<Categoria> AllCategoriesPageable(Pageable pageable) {
		// busca las categorias que no se encuentran elimnadas
		return categoriaRepo.findByEliminated(pageable, false);
	}

	@Transactional
	public Categoria RegisterCategoria(Categoria categoria) {
		categoria.setEliminated(false);
		return categoriaRepo.save(categoria);
	}

	@Transactional(readOnly = true)
	public Categoria BuscarCategoriaById(Long id) {
		return categoriaRepo.findByIdAndEliminated(id, false);
	}

	@Transactional(readOnly = true)
	public List<Categoria> findCategoriasProductos() {
		TipoCategoria tipo = tipoRepo.findByTipo("Producto");
		return categoriaRepo.findByTipoAndEliminated(tipo, false);
	}

	@Transactional(readOnly = true)
	public List<Categoria> findCategoriasCombos() {
		TipoCategoria tipo = tipoRepo.findByTipo("Combo");
		return categoriaRepo.findByTipoAndEliminated(tipo, false);
	}

	@Transactional
	public void deleteCategoriabyId(Categoria categoria) {
		categoria.setImagen("");
		categoria.setEliminated(true);
		categoriaRepo.save(categoria);
	}

	@Transactional
	public void deleteCategoriaDefinitiveById(Long id) {
		categoriaRepo.deleteById(id);
	}

}
