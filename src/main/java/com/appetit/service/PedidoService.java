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
	public List<Pedido> obtenerPedidosDiaFechaAndEstado(Date fecha, Estado estado) {
		return pedidoRepo.findByFechaAndEstado(fecha, estado);
	}

	@Transactional(readOnly = true)
	public Page<Pedido> obtenerPedidosDelDiaPageable(Pageable pageable) {
		return pedidoRepo.findAll(pageable);
	}

	@Transactional
	public Pedido registrarNuevoPedido(Pedido pedido) {
		return pedidoRepo.save(pedido);
	}

}
