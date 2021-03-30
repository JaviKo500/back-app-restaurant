package com.appetit.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.appetit.models.MedioPago;
import com.appetit.models.MovimientoCaja;
import com.appetit.models.Usuario;
import com.appetit.repository.IMovimientoRepo;

@Service
public class MovimientoService {

	@Autowired
	IMovimientoRepo movimientoRepo;

	@Transactional
	public MovimientoCaja registrarMovimiento(MovimientoCaja movimiento) {
		return movimientoRepo.save(movimiento);
	}

	@Transactional(readOnly = true)
	public List<MedioPago> ObtenerMediosDePago() {
		return movimientoRepo.obtenerMediosDePago();
	}

	@Transactional(readOnly = true)
	public Page<MovimientoCaja> obtenerMovimientosCajaUsuarioFecha(Pageable pageable, Usuario usuario) {
		return movimientoRepo.findByUsuarioAndFecha(pageable, usuario, new Date());
	}
}
