package com.appetit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.appetit.models.Caja;
import com.appetit.repository.ICajaRepo;

@Service
public class CajaService {
	@Autowired
	ICajaRepo cajaRepo;

	@Transactional
	public Caja registrarCaja(Caja caja) {
		return cajaRepo.save(caja);
	}

	@Transactional(readOnly = true)
	public Page<Caja> listarCajasPaginado(Pageable pageable) {
		return cajaRepo.findByEliminated(pageable, false);
	}

	@Transactional
	public void eliminarLogicamenteCaja(Caja caja) {
		caja.setEliminated(true);
		caja.setNombreCaja(caja.getNombreCaja() + caja.getId());
		caja.setNumeroCaja(caja.getId() + "e" + caja.getNumeroCaja());
		cajaRepo.save(caja);
	}

	@Transactional(readOnly = true)
	public Caja obtenerCajaId(Long id) {
		return cajaRepo.findByIdAndEliminated(id, false);
	}

	@Transactional(readOnly = true)
	public List<Caja> obtenerTodasCajasDisponibles() {
		return cajaRepo.findByEliminatedAndEstado(false, true);
	}

}
