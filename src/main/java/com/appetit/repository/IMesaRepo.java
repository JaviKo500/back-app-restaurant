package com.appetit.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.appetit.models.Mesa;

public interface IMesaRepo extends JpaRepository<Mesa, Long> {

	public Mesa findByNombre(String nombre);

	public List<Mesa> findByEstadoAndEliminated(Boolean estado, Boolean eliminated);

	public Mesa findByNombreAndEliminated(String nombre, Boolean eliminated);

	Page<Mesa> findByEliminated(Pageable pageable, Boolean eliminated);

	public Mesa findByIdAndEliminated(Long id, Boolean eliminated);
}
