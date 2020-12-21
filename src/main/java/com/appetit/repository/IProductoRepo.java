package com.appetit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.appetit.models.Producto;

@Repository
public interface IProductoRepo extends JpaRepository<Producto, Long> {

}
