package com.appetit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.appetit.models.TipoCategoria;

public interface ITipoCategoriaRepo extends JpaRepository<TipoCategoria, Long> {

	public TipoCategoria findByTipo(String tipo);


}
