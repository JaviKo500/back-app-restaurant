package com.appetit.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Table(name = "cliente")
@Entity
public class Cliente implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	@NotNull(message = "La cédula es un campo obligatorio.")
	private String cedula;

	@NotNull(message = "Los nombres son obligatorios.")
	private String nombres;

	@NotNull(message = "Los apellidos son un campo obligatorio.")
	private String apellidos;

	private String celular;

	private String direccion;

	@Column(unique = true)
	@NotNull(message = "El email es un campo obligatorio.")
	private String email;

	private Boolean eliminated;

	public Boolean getEliminated() {
		return eliminated;
	}

	public void setEliminated(Boolean eliminated) {
		this.eliminated = eliminated;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	private static final long serialVersionUID = 1L;
}
