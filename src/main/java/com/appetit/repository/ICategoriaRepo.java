package com.appetit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.appetit.models.Categoria;
@Repository
public interface ICategoriaRepo extends JpaRepository<Categoria, Long> {

	public Categoria findByNombre(String nombre);
	
}
