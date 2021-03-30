package com.appetit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.appetit.models.Categoria;
import com.appetit.models.Combo;
import com.appetit.repository.IComboRepo;

@Service
public class ComboService {

	@Autowired
	IComboRepo comboRepo;
	
	@Transactional(readOnly = true)
	public Combo buscarbyId(Long id) {
		return comboRepo.findByIdAndEliminado(id, false);
	}

	@Transactional(readOnly = true)
	public Page<Combo> obtenerCombosPage(Pageable pageable) {
		return comboRepo.findByEliminado(pageable, false);
	}

	@Transactional(readOnly = true)
	public List<Combo>obtenerCombosCliente(Categoria categoria){
		return comboRepo.findByEstadoAndEliminadoAndCategoria(true, false, categoria);
	}
	
	@Transactional
	public Combo registrarCombo(Combo combo) {
		combo.setEliminado(false);
		return comboRepo.save(combo);
	}
	
	@Transactional
	public Combo AsignarInforExtraCombo(Combo combo) {
		return comboRepo.save(combo);
	}
	
	@Transactional(readOnly = true)
	public List<Combo> filtrarCombosNombre(String termino) {
		return comboRepo.findByEstadoAndEliminadoAndNombreContainingIgnoreCase(true, false, termino);
	}
}
