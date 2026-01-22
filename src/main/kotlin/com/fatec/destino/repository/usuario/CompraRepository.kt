package com.fatec.destino.repository.usuario

import com.fatec.destino.model.Compra
import com.fatec.destino.model.pacote.Pacote
import com.fatec.destino.model.usuario.Usuario
import com.fatec.destino.util.model.pacote.PacoteStatus
import com.fatec.destino.util.repository.CompraMesProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CompraRepository : JpaRepository<Compra, Long> {
    @Query("""
        SELECT MONTH(c.dataCompra) as mes, COUNT(c) as total 
        FROM Compra c 
        WHERE YEAR(c.dataCompra) = :ano 
        GROUP BY MONTH(c.dataCompra) 
        ORDER BY MONTH(c.dataCompra) ASC
    """)
    fun countComprasByMonth(@Param("ano") ano: Int): List<CompraMesProjection>

    @Query("SELECT c FROM Compra c WHERE c.usuario.id = :usuarioId ORDER BY c.pacote.inicio DESC")
    fun findAllByUsuarioId(@Param("usuarioId") usuarioId: UUID?): MutableList<Compra?>?

    @Query("SELECT c FROM Compra c WHERE c.usuario.id = :usuarioId AND c.pacote.status = :status ORDER BY c.pacote.inicio DESC")
    fun findAllByUsuarioIdWhereStatusConcluido(
        @Param("usuarioId") usuarioId: UUID?,
        @Param("status") status: PacoteStatus?
    ): MutableList<Compra?>?

    @Query("SELECT c FROM Compra c WHERE c.usuario.id = :usuarioId AND c.pacote.status = :status ORDER BY c.pacote.inicio DESC")
    fun findAllByUsuarioIdWhereStatusEmAdamento(
        @Param("usuarioId") usuarioId: UUID?,
        @Param("status") status: PacoteStatus?
    ): MutableList<Compra?>?

    // Busca compra específica garantindo que pertence ao usuario (segurança)
    @Query("SELECT c FROM Compra c WHERE c.id = :compraId AND c.usuario.email = :emailUsuario")
    fun findByIdAndUsuarioEmail(
        @Param("compraId") compraId: Long?,
        @Param("emailUsuario") emailUsuario: String?
    ): Compra?

    // Em CompraRepository.java
    fun findByUsuarioAndPacote(usuario: Usuario?, pacote: Pacote?): Optional<Compra?>?
}