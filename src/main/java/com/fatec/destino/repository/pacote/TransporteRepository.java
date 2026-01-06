package com.fatec.destino.repository.pacote;

import com.fatec.destino.model.pacote.transporte.Transporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransporteRepository extends JpaRepository<Transporte, Long> {
    @Query("SELECT t.preco FROM Transporte t WHERE t.id = :id")
    int findPrecoPorId(@Param("id") long id);
}
