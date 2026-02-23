package com.fatec.destino.repository.viagem

import com.fatec.destino.model.viagem.Viagem
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
interface ViagemRepository : JpaRepository<Viagem, Long> {

    @Query("SELECT v from Viagem v where v.status = 'EMANDAMENTO'")
    fun encontreViagens(pageable: Pageable): Page<Viagem>

    @Query("""
        SELECT v FROM Viagem v WHERE v.status = 'EMANDAMENTO' 
        AND (:nome IS NULL OR LOWER(v.pacote.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) 
        AND (:precoMax IS NULL OR v.pacote.preco <= :precoMax)
    """)
    fun buscarComFiltros(
        nome: String?,
        precoMax: BigDecimal?,
        pageable: Pageable
    ): Page<Viagem>

    @Query("SELECT v.status as status, COUNT(v) as total FROM Viagem v GROUP BY v.status")
    fun countViagensByStatus(): List<StatusCount>

    @Query("SELECT t.meio as meio, COUNT(v) as total FROM Viagem v JOIN v.pacote p JOIN p.transporte t GROUP BY t.meio")
    fun countViagensByTransporteMeio(): List<MeioCount>

    @Query("""
        SELECT MONTH(v.inicio) as mes, COUNT(v) as total 
        FROM Viagem v 
        WHERE v.status = 'CONCLUIDO' AND YEAR(v.inicio) = :ano 
        GROUP BY MONTH(v.inicio) 
        ORDER BY MONTH(v.inicio) ASC
    """)
    fun countViagensConcluidasByMonth(ano: Int): List<MonthCount>
}
