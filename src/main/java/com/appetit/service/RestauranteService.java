package com.appetit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appetit.models.Restaurante;
import com.appetit.repository.IrestauranteRepo;

@Service
public class RestauranteService {

	@Autowired
	public IrestauranteRepo restauranteRepo;

	public Restaurante SaveDatosRestaurante(Restaurante restaurante) {
		return restauranteRepo.save(restaurante);
	}

}
