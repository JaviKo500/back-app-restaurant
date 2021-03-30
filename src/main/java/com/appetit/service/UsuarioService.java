package com.appetit.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.appetit.models.Role;
import com.appetit.models.Sexo;
import com.appetit.models.Usuario;
import com.appetit.repository.IUsuarioRepo;

@Service
public class UsuarioService implements UserDetailsService {
	@Autowired
	IUsuarioRepo usuarioRepo;

	@Transactional(readOnly = true)
	public List<Usuario> ObtenerListaUsuariosArqueos() {
		return usuarioRepo.findByEstadoAndEliminated(true, false);
	}

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
		return usuarioRepo.findByEliminatedAndId(false, id);
	}

	@Transactional(readOnly = true)
	public Usuario buscarusuarioByIdMovimiento(Long id) {
		return usuarioRepo.findByEliminatedAndIdAndEstado(false, id, true);
	}

	@Transactional(readOnly = true)
	public Usuario findByUsername(String username) {
		return usuarioRepo.findByUsername(username);
	}

	@Transactional(readOnly = true)
	public Usuario BuscarUsuarioByUsername(String username) {
		return usuarioRepo.findByEliminatedAndUsername(false, username);
	}

	@Transactional(readOnly = true)
	public List<Role> obtenerRoles() {
		return usuarioRepo.findAllRoles();
	}

	@Transactional(readOnly = true)
	public Page<Usuario> listarUsuariosPage(Pageable pageable) {
		return usuarioRepo.findByEliminated(pageable, false);
	}

	@Transactional
	public void EliminarUsuario(Long id) {
		usuarioRepo.deleteById(id);
	}

	@Transactional
	public void EliminarLogicamenteUsuario(Usuario u) {
		u.setEliminated(true);
		u.setEstado(false);
		u.setEmail(u.getEmail() + "-id" + u.getId());
		u.setCedula(u.getCedula() + "-id" + u.getId());
		u.setTelefono(u.getTelefono() + "-id" + u.getId());
		usuarioRepo.save(u);
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario u = usuarioRepo.findByUsername(username);
		if (u == null) {
			System.out.println("Error en el login no existe el usuario " + username);
			throw new UsernameNotFoundException("Error en el login no existe el usuario " + username);
		}
		List<GrantedAuthority> authorities = u.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getNombre()))
				.peek(authority -> System.out.println(authority.getAuthority())).collect(Collectors.toList());
		return new User(u.getUsername(), u.getPassword(), u.getEstado(), true, true, true, authorities);
	}

}
