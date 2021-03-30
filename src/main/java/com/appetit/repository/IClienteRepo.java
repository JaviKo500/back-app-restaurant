package com.appetit.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.appetit.models.Cliente;

@Repository
public interface IClienteRepo extends JpaRepository<Cliente, Long> {

	public Cliente findByCedula(String cedula);

	public Page<Cliente> findByEliminated(Pageable pageable, Boolean eliminated);

	public Cliente findByEliminatedAndId(Boolean eliminated, Long id);

}
