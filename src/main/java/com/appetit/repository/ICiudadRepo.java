package com.appetit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.appetit.models.Ciudad;

@Repository
public interface ICiudadRepo extends JpaRepository<Ciudad, Long> {

}
