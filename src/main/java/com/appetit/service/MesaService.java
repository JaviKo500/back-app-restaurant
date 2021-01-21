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
	public List<Mesa> getAllMesas() {
		return mesaRepo.findAll();
	}

	@Transactional
	public Mesa RegistrarMesa(Mesa mesa) {
		return mesaRepo.save(mesa);
	}

	@Transactional(readOnly = true)
	public Page<Mesa> ObtenerMesaPage(Pageable pageable) {
		return mesaRepo.findAll(pageable);
	}

	@Transactional(readOnly = true)
	public Mesa getMesaById(Long id) {
		return mesaRepo.findById(id).orElse(null);
	}

	@Transactional
	public void BorrarMesa(Long id) {
		mesaRepo.deleteById(id);
	}
}
