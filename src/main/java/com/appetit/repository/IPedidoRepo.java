package com.appetit.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.appetit.models.Estado;
import com.appetit.models.Pedido;

@Repository
public interface IPedidoRepo extends JpaRepository<Pedido, Long> {
	public List<Pedido> findByFechaAndEstado(Date fecha, Estado estado);

	public List<Pedido> findByFechaAndIsAnuladoAndIsEntregado(Date fecha, Boolean isAnulado, Boolean isEntregado);

	@Query(value = "select p from Pedido p where p.estado.nomEstado ='Entregado' or p.estado.nomEstado ='Anulado'")
	public Page<Pedido> seleccionar(Pageable pageable);

	public Page<Pedido> findByFechaBetweenAndEstado(Pageable pageable, Date fecha_ini, Date fecha_fin, Estado estado);
}
