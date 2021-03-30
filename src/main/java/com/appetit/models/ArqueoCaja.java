package com.appetit.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class ArqueoCaja implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	@ManyToOne(fetch = FetchType.LAZY)
	private Caja caja;

	private Double montoInicial;

	private Double ingresos;

	private Double montoFinal;
	private Double efectivo;
	private Double bancos;

	private Double diferencia;

	private String descripcion;

	private String descripcionCierre;

	@Temporal(TemporalType.DATE)
	private Date fecha;

	private Date fechaApertura;

	private Date fechaCierre;

	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	@ManyToOne(fetch = FetchType.LAZY)
	private Usuario usuario;

	private Boolean isOpen;

	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<MovimientoCaja> movimientos = new ArrayList<MovimientoCaja>();

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Double getEfectivo() {
		return efectivo;
	}

	public void setEfectivo(Double efectivo) {
		this.efectivo = efectivo;
	}

	public Double getBancos() {
		return bancos;
	}

	public void setBancos(Double bancos) {
		this.bancos = bancos;
	}

	public String getDescripcionCierre() {
		return descripcionCierre;
	}

	public void setDescripcionCierre(String descripcionCierre) {
		this.descripcionCierre = descripcionCierre;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Caja getCaja() {
		return caja;
	}

	public void setCaja(Caja caja) {
		this.caja = caja;
	}

	public Double getMontoInicial() {
		return montoInicial;
	}

	public void setMontoInicial(Double montoInicial) {
		this.montoInicial = montoInicial;
	}

	public Double getIngresos() {
		return ingresos;
	}

	public void setIngresos(Double ingresos) {
		this.ingresos = ingresos;
	}

	public Double getMontoFinal() {
		return montoFinal;
	}

	public void setMontoFinal(Double montoFinal) {
		this.montoFinal = montoFinal;
	}

	public Double getDiferencia() {
		return diferencia;
	}

	public void setDiferencia(Double diferencia) {
		this.diferencia = diferencia;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Date getFechaApertura() {
		return fechaApertura;
	}

	public void setFechaApertura(Date fechaApertura) {
		this.fechaApertura = fechaApertura;
	}

	public Date getFechaCierre() {
		return fechaCierre;
	}

	public List<MovimientoCaja> getMovimientos() {
		return movimientos;
	}

	public void setMovimientos(List<MovimientoCaja> movimientos) {
		this.movimientos = movimientos;
	}

	public void setFechaCierre(Date fechaCierre) {
		this.fechaCierre = fechaCierre;
	}

	public Boolean getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(Boolean isOpen) {
		this.isOpen = isOpen;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	private static final long serialVersionUID = 1L;

}