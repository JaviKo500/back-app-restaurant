package com.appetit.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Caja implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String numeroCaja;

	private String nombreCaja;

	private Boolean estado;
	
	private Boolean eliminated;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getEliminated() {
		return eliminated;
	}

	public void setEliminated(Boolean eliminated) {
		this.eliminated = eliminated;
	}

	public String getNumeroCaja() {
		return numeroCaja;
	}

	public void setNumeroCaja(String numeroCaja) {
		this.numeroCaja = numeroCaja;
	}

	public String getNombreCaja() {
		return nombreCaja;
	}

	public void setNombreCaja(String nombreCaja) {
		this.nombreCaja = nombreCaja;
	}

	public Boolean getEstado() {
		return estado;
	}

	public void setEstado(Boolean estado) {
		this.estado = estado;
	}

	private static final long serialVersionUID = 1L;
}