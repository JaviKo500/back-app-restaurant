package com.appetit.repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.appetit.models.ArqueoCaja;
import com.appetit.models.Caja;
import com.appetit.models.Usuario;

public interface IArqueoCajaRepo extends JpaRepository<ArqueoCaja, Long> {

	public Page<ArqueoCaja> findByFecha(Pageable pageable, Date fecha);

	public ArqueoCaja findByCajaAndUsuarioAndIsOpen(Caja caja, Usuario usuario, Boolean isOpen);

	public ArqueoCaja findByUsuarioAndIsOpen(Usuario usuario, Boolean isOpen);

	public Page<ArqueoCaja> findByFechaBetween(Pageable pageable, Date desde, Date hasta);
}
