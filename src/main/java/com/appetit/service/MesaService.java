package com.appetit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.appetit.models.Mesa;
import com.appetit.repository.IMesaRepo;

@Service
public class MesaService {
	@Autowired
	IMesaRepo mesaRepo;

	@Transactional(readOnly = true)
	public List<Mesa> getAllMesasClienteEstado() {
		return mesaRepo.findByEstadoAndEliminated(true, false);
	}

	@Transactional(readOnly = true)
	public Mesa ObtenerMesaNombre(String nombre) {
		return mesaRepo.findByNombreAndEliminated(nombre, false);
	}

	@Transactional
	public Mesa RegistrarMesa(Mesa mesa) {
		mesa.setEliminated(false);
		return mesaRepo.save(mesa);
	}

	@Transactional(readOnly = true)
	public Page<Mesa> ObtenerMesaPage(Pageable pageable) {
		return mesaRepo.findByEliminated(pageable, false);
	}

	@Transactional(readOnly = true)
	public Mesa getMesaById(Long id) {
		return mesaRepo.findByIdAndEliminated(id, false);
	}

	@Transactional
	public void BorrarMesa(Mesa mesa) {
		mesa.setEliminated(true);
		mesa.setNombre(mesa.getNombre() + "-> Eliminado " + mesa.getId());
		mesaRepo.save(mesa);
	}
}
