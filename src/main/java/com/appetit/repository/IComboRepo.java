package com.appetit.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.appetit.models.Categoria;
import com.appetit.models.Combo;

public interface IComboRepo extends JpaRepository<Combo, Long> {

	public Page<Combo> findByEliminado(Pageable pageable, Boolean eliminado);

	public List<Combo> findByEstadoAndEliminadoAndCategoria(Boolean estado, Boolean eliminado, Categoria categoria);

	public Combo findByIdAndEliminado(Long id, Boolean eliminado);

	public List<Combo> findByEstadoAndEliminadoAndNombreContainingIgnoreCase(Boolean estado, Boolean eliminado,
			String term);

}
