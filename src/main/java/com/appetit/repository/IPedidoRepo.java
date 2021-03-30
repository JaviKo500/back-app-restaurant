package com.appetit.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.appetit.models.Estado;
import com.appetit.models.Pedido;

@Repository
public interface IPedidoRepo extends JpaRepository<Pedido, Long> {
	public List<Pedido> findByFechaAndEstado(Date fecha, Estado estado);
	
	public List<Pedido> findByFechaAndIsAnuladoAndIsEntregado(Date fecha, Boolean isAnulado, Boolean isEntregado);

}
