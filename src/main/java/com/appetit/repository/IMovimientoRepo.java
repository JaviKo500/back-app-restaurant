package com.appetit.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.appetit.models.MedioPago;
import com.appetit.models.MovimientoCaja;
import com.appetit.models.Usuario;

public interface IMovimientoRepo extends JpaRepository<MovimientoCaja, Long> {

	@Query("from MedioPago")
	public List<MedioPago> obtenerMediosDePago();

	public Page<MovimientoCaja> findByUsuarioAndFecha(Pageable pageable, Usuario usuario, Date fecha);

	public Page<MovimientoCaja> findByFechaBetween(Pageable pageable, Date fecha_ini, Date fecha_fin);

}
