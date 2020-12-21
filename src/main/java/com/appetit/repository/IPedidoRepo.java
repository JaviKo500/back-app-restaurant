package com.appetit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.appetit.models.Pedido;

@Repository
public interface IPedidoRepo extends JpaRepository<Pedido, Long> {

}
