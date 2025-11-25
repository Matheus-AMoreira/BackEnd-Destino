package com.destino.projeto_destino.repository.pacote;

import com.destino.projeto_destino.model.pacote.transporte.Transporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransporteRepository extends JpaRepository<Transporte, Integer> {
    @Query("SELECT t.preco FROM Transporte t WHERE t.id = :id")
    int findPrecoPorId(@Param("id") Integer id);
}
