package com.appetit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.appetit.models.Cliente;
import com.appetit.repository.IClienteRepo;

@Service
public class ClienteService {

	@Autowired
	private IClienteRepo clienteRepo;

	public Page<Cliente> listarClientesPaginado(Pageable pageable) {
		return clienteRepo.findByEliminated(pageable, false);
	}

	public Cliente RegisterCliente(Cliente cliente) {
		return clienteRepo.save(cliente);
	}

	public Cliente FindClineteByCedula(String cedula) {
		return clienteRepo.findByCedula(cedula);
	}

	public Cliente findClienteByID(Long id) {
		return clienteRepo.findById(id).orElse(null);
	}
	
	public Cliente findClienteByIDAndEliminated(Long id) {
		return clienteRepo.findByEliminatedAndId(false, id);
	}

	public void deleteClienteByID(Long id) {
		clienteRepo.deleteById(id);
	}

	public void deleteClienteLogicamente(Cliente cliente) {
		cliente.setEliminated(true);
		cliente.setCedula(cliente.getId() + " " + cliente.getCedula());
		cliente.setEmail(cliente.getId() + " " + cliente.getEmail());
		cliente.setCelular(cliente.getId() + " " + cliente.getCelular());
		clienteRepo.save(cliente);
	}
}
