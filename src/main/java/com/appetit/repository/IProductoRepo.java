package com.appetit.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.appetit.models.Categoria;
import com.appetit.models.Producto;

@Repository
public interface IProductoRepo extends JpaRepository<Producto, Long> {

	public List<Producto> findByEstadoAndCategoriaAndEliminated(Boolean estado, Categoria categoria,
			Boolean eliminated);

	public List<Producto> findByEstadoAndEliminatedAndNombreContainingIgnoreCase(Boolean estado, Boolean eliminated,
			String term);

	public Page<Producto> findByEliminated(Pageable pageable, Boolean eliminated);
	
	public Producto findByIdAndEliminated(Long id, Boolean eliminated);
	
}
