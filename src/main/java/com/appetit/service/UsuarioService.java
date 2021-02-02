package com.appetit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.appetit.models.Role;
import com.appetit.models.Sexo;
import com.appetit.models.Usuario;
import com.appetit.repository.IUsuarioRepo;

@Service
public class UsuarioService {
	@Autowired
	IUsuarioRepo usuarioRepo;

	@Transactional
	public Usuario registrarUsuario(Usuario usuario) {
		return usuarioRepo.save(usuario);
	}

	@Transactional(readOnly = true)
	public List<Sexo> obtenerlistaSexos() {
		return usuarioRepo.findAllSexo();
	}

	@Transactional(readOnly = true)
	public Usuario buscarusuarioById(Long id) {
		return usuarioRepo.findById(id).orElse(null);
	}

	@Transactional(readOnly = true)
	public Usuario BuscarUsuarioByUsername(String username) {
		return usuarioRepo.findByUsername(username);
	}

	@Transactional(readOnly = true)
	public List<Role> obtenerRoles() {
		return usuarioRepo.findAllRoles();
	}

	@Transactional(readOnly = true)
	public Page<Usuario> listarUsuariosPage(Pageable pageable) {
		return usuarioRepo.findAll(pageable);
	}

	@Transactional
	public void EliminarUsuario(Long id) {
		usuarioRepo.deleteById(id);
	}

}
