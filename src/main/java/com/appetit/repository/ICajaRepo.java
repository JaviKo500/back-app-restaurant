package com.appetit.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.appetit.models.Caja;

public interface ICajaRepo extends JpaRepository<Caja, Long> {
	public Page<Caja> findByEliminated(Pageable pageable, Boolean eliminated);

	public Caja findByIdAndEliminated(Long id, Boolean eliminated);

	public List<Caja> findByEliminatedAndEstado(Boolean eliminated, Boolean estado);

}
