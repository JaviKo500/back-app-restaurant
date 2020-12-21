package com.appetit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.appetit.models.Restaurante;

public interface IrestauranteRepo extends JpaRepository<Restaurante, Long> {

}
