package com.destino.projeto_destino.repository;

import com.destino.projeto_destino.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

    // Agrupa compras por Mês. Retorna [Mês (Integer), Quantidade (Long)]
    // Ex: [1, 50], [2, 35]...
    @Query("SELECT MONTH(c.dataCompra), COUNT(c) FROM Compra c GROUP BY MONTH(c.dataCompra)")
    List<Object[]> countComprasByMonth();
}
