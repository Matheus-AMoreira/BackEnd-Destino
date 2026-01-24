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

    @Query("SELECT p from Pacote p where p.status = 'EMANDAMENTO'")
    fun encontrePacotes(pageable: Pageable): Page<Pacote>

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
        c.pacote.id, 
        c.pacote.nome, 
        CAST(c.pacote.preco AS int), 
        c.pacote.hotel.nome, 
        c.pacote.transporte.empresa
    ) 
    FROM Compra c 
    GROUP BY c.pacote.id, c.pacote.nome, c.pacote.preco, c.pacote.hotel.nome, c.pacote.transporte.empresa 
    ORDER BY COUNT(c.pacote) DESC
""")
    fun procuraPacotesMaisVendidos(): List<PacoteResponseDTO>

    @Query("SELECT p FROM Pacote p WHERE p.status IN ('EMANDAMENTO', 'CONCLUIDO') ORDER BY p.status DESC, p.inicio DESC")
    fun findPacotesPublicos(): List<Pacote>

    @Query("""
        SELECT p FROM Pacote p WHERE p.status = 'EMANDAMENTO' 
        AND (:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) 
        AND (:precoMax IS NULL OR p.preco <= :precoMax)
    """)
    fun buscarComFiltros(
        nome: String?,
        precoMax: BigDecimal?,
        pageable: Pageable
    ): Page<Pacote>

    fun findByNome(nome: String): Pacote?

    fun existsByHotelId(id: Long): Boolean

    fun existsByTransporteId(id: Long): Boolean

    // --- Dashboard com Projeções Tipadas ---

    @Query("SELECT p.status as status, COUNT(p) as total FROM Pacote p GROUP BY p.status")
    fun countPacotesByStatus(): List<StatusCount>

    @Query("SELECT t.meio as meio, COUNT(p) as total FROM Pacote p JOIN p.transporte t GROUP BY t.meio")
    fun countPacotesByTransporteMeio(): List<MeioCount>

    @Query("""
        SELECT MONTH(p.inicio) as mes, COUNT(p) as total 
        FROM Pacote p 
        WHERE p.status = 'CONCLUIDO' AND YEAR(p.inicio) = :ano 
        GROUP BY MONTH(p.inicio) 
        ORDER BY MONTH(p.inicio) ASC
    """)
    fun countViagensConcluidasByMonth(ano: Int): List<MonthCount>
}