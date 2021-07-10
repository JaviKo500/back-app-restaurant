package com.appetit.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.appetit.models.Estado;
import com.appetit.models.Pedido;
import com.appetit.repository.IPedidoRepo;

@Service
public class PedidoService {

	@Autowired
	IPedidoRepo pedidoRepo;

	@Transactional(readOnly = true)
	public Pedido obtenerPedidoId(Long id) {
		return pedidoRepo.findById(id).orElse(null);
	}

	@Transactional(readOnly = true)
	public List<Pedido> obtenerPedidosDiaFechaAndEstado(Date fecha, Estado estado) {
		return pedidoRepo.findByFechaAndEstado(fecha, estado);
	}

	@Transactional(readOnly = true)
	public List<Pedido> obtenerPedidosDiaAndNoDispoNoAnulado(Date fecha) {
		return pedidoRepo.findByFechaAndIsAnuladoAndIsEntregado(fecha, false, false);
	}

	@Transactional(readOnly = true)
	public Page<Pedido> obtenerPedidosDelDiaPageable(Pageable pageable) {
		return pedidoRepo.findAll(pageable);
	}

	@Transactional
	public Pedido registrarNuevoPedido(Pedido pedido) {
		return pedidoRepo.save(pedido);
	}

	@Transactional(readOnly = true)
	public Page<Pedido> obtenerVentas(Pageable pageable) {
		return pedidoRepo.seleccionar(pageable);
	}

	@Transactional(readOnly = true)
	public Page<Pedido> obtenerVentasFechasEstado(Pageable pageable, Estado estado, Date fechaIni, Date fechaFin) {
		return pedidoRepo.findByFechaBetweenAndEstado(pageable, fechaIni, fechaFin, estado);
	}

}
