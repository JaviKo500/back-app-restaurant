package com.appetit.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.appetit.models.Categoria;
import com.appetit.models.TipoCategoria;

@Repository
public interface ICategoriaRepo extends JpaRepository<Categoria, Long> {

	public Categoria findByNombreAndEliminated(String nombre, Boolean eliminated);

	public List<Categoria> findByEliminated(Boolean eliminated);

	public Categoria findByIdAndEliminated(Long id, Boolean eliminated);

	public Page<Categoria> findByEliminated(Pageable pageable, Boolean eliminated);

	public List<Categoria> findByTipoAndEliminated(TipoCategoria tipo, Boolean eliminated);

}
