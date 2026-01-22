package com.fatec.destino.repository.pacote.transporte

import com.fatec.destino.model.pacote.transporte.Transporte
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface TransporteRepository : JpaRepository<Transporte, Long> {
    @Query("SELECT t.preco FROM Transporte t WHERE t.id = :id")
    fun findPrecoPorId(@Param("id") id: Long): Int
}