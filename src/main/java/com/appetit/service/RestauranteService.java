package com.appetit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.appetit.models.Restaurante;
import com.appetit.repository.IrestauranteRepo;

@Service
public class RestauranteService {

	@Autowired
	public IrestauranteRepo restauranteRepo;

	@Transactional
	public Restaurante SaveDatosRestaurante(Restaurante restaurante) {
		return restauranteRepo.save(restaurante);
	}
	
	@Transactional(readOnly = true)
	public Restaurante obtenerDatosRestaurante(Long id) {
		return restauranteRepo.findById(id).orElse(null);
	}

}
