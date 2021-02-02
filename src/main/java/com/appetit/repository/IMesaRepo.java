package com.appetit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.appetit.models.Mesa;

public interface IMesaRepo extends JpaRepository<Mesa, Long> {

	public Mesa findByNombre(String nombre);

}
