package com.appetit.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.appetit.models.Role;
import com.appetit.models.Sexo;
import com.appetit.models.Usuario;

@Repository
public interface IUsuarioRepo extends JpaRepository<Usuario, Long> {

	public Usuario findByUsername(String username);

	@Query("from Role")
	public List<Role> findAllRoles();

	@Query("from Sexo")
	public List<Sexo> findAllSexo();

	public List<Usuario> findByEstadoAndEliminated(Boolean estado, Boolean eliminated);

	public Page<Usuario> findByEliminated(Pageable pageable, Boolean eliminated);

	public Usuario findByEliminatedAndUsername(Boolean eliminated, String username);

	public Usuario findByEliminatedAndId(Boolean eliminated, Long id);

	public Usuario findByEliminatedAndIdAndEstado(Boolean eliminated, Long id, Boolean estado);

}
