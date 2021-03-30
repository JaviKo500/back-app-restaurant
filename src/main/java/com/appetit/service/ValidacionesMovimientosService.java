package com.appetit.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.appetit.models.MovimientoCaja;

@Service
public class ValidacionesMovimientosService {

	public List<String> validacionesMovimientos(MovimientoCaja m) {
		List<String> err = new ArrayList<>();
		if (m.getPedido() == null) {
			err.add("El pedido no existe");
		}
		if (m.getTipoPago() == null) {
			err.add("Es requerido un tipo de pago.");
		}
		return err;
	}

	public List<String> finalizarPedidoMovimientos(MovimientoCaja m) {
		List<String> err = new ArrayList<>();
		if (m.getPedido() == null) {
			err.add("El pedido no existe");
		}
		if (m.getTipoPago() == null) {
			err.add("Es requerido un tipo de pago.");
		} else {
			if (m.getTipoPago().getId() != 1) {
				if (m.getTipoPago().getFolio() == null) {
					err.add("El código de transaccion es requerido.");
				} else {
					if (m.getTipoPago().getFolio().length() == 0) {
						err.add("El código de transaccion es requerido.");
					}
				}
			}
		}
		if (m.getPedido().getEstado() == null) {
			err.add("Seleccione un estado, entregado o anulado para finalizar.");
		}
		if (m.getUsuario().getId() == null || m.getUsuario().getUsername() == null) {
			err.add("No existe un usuario encargado.");
		}
		return err;
	}
}
