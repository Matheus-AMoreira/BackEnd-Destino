package com.fatec.destino.repository.pacote

import com.fatec.destino.dto.pacote.PacoteResponseDTO
import com.fatec.destino.model.pacote.Pacote
import com.fatec.destino.util.repository.MeioCount
import com.fatec.destino.util.repository.MonthCount
import com.fatec.destino.util.repository.StatusCount
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface PacoteRepository : JpaRepository<Pacote, Long> {

    @Query("""
        SELECT new com.fatec.destino.dto.pacote.PacoteResponseDTO(
        p.id, 
        p.nome, 
        CAST(p.preco AS int), 
        p.hotel.nome, 
        p.transporte.empresa
    ) 
        FROM Pacote p WHERE p.nome = :nome
    """)
    fun procurePeloNome(nome: String): List<PacoteResponseDTO>

    @Query("""
    SELECT new com.fatec.destino.dto.pacote.PacoteResponseDTO(
        c.viagem.pacote.id, 
        c.viagem.pacote.nome, 
        CAST(c.viagem.pacote.preco AS int), 
        c.viagem.pacote.hotel.nome, 
        c.viagem.pacote.transporte.empresa
    ) 
    FROM Compra c 
    GROUP BY c.viagem.pacote.id, c.viagem.pacote.nome, c.viagem.pacote.preco, c.viagem.pacote.hotel.nome, c.viagem.pacote.transporte.empresa 
    ORDER BY COUNT(c.viagem.pacote) DESC
""")
    fun procuraPacotesMaisVendidos(): List<PacoteResponseDTO>

    fun findByNome(nome: String): Pacote?

    fun existsByHotelId(id: Long): Boolean

    fun existsByTransporteId(id: Long): Boolean
}