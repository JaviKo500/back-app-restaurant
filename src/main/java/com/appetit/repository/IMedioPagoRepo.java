package com.appetit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.appetit.models.MedioPago;

@Repository
public interface IMedioPagoRepo extends JpaRepository<MedioPago, Long> {

}
