package com.appetit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.appetit.models.Estado;

@Repository
public interface IEstadoRepo extends JpaRepository<Estado, Long> {

	public Estado findBynomEstado(String nomEstado);

}
