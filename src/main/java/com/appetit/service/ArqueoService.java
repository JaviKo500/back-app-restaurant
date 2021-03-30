package com.appetit.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.appetit.models.ArqueoCaja;
import com.appetit.models.Caja;
import com.appetit.models.Usuario;
import com.appetit.repository.IArqueoCajaRepo;

@Service
public class ArqueoService {
	@Autowired
	IArqueoCajaRepo arqueoRepo;

	@Transactional
	public ArqueoCaja registrarNuevoArqueo(ArqueoCaja arqueo) {
		return arqueoRepo.save(arqueo);
	}

	@Transactional(readOnly = true)
	public Page<ArqueoCaja> obtenerArqueosFecha(Pageable pageable, Date fecha) {
		return arqueoRepo.findByFecha(pageable, fecha);
	}

	@Transactional(readOnly = true)
	public ArqueoCaja obtenerArqueoPorId(Long id) {
		return arqueoRepo.findById(id).orElse(null);
	}

	@Transactional(readOnly = true)
	public ArqueoCaja obtenerArqByCajaUserIsOpen(Caja caja, Usuario user) {
		return arqueoRepo.findByCajaAndUsuarioAndIsOpen(caja, user, true);
	}

	@Transactional(readOnly = true)
	public ArqueoCaja getArqueoByUsuarioIsOpen(Usuario usuario) {
		return arqueoRepo.findByUsuarioAndIsOpen(usuario, true);
	}

	@Transactional(readOnly = true)
	public Page<ArqueoCaja> ArqueosEntreFechas(Pageable pageable, Date desde, Date hasta) {
		return arqueoRepo.findByFechaBetween(pageable, desde, hasta);
	}
}
