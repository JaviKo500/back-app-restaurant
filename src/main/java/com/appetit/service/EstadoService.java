package com.appetit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.appetit.models.Estado;
import com.appetit.repository.IEstadoRepo;

@Service
public class EstadoService {

	@Autowired
	IEstadoRepo estadoRepo;

	@Transactional(readOnly = true)
	public Estado buscarEstadoByid(Long id) {
		return estadoRepo.findById(id).orElse(null);
	}

	@Transactional(readOnly = true)
	public Estado buscarEstadoByNombre(String nombre) {
		return estadoRepo.findBynomEstado(nombre);
	}

	@Transactional(readOnly = true)
	public List<Estado> obtenerListaEstados() {
		return estadoRepo.findAll();
	}
}
