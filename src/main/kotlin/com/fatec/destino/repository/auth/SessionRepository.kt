package com.fatec.destino.repository.auth

import com.fatec.destino.model.usuario.SessionToken
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SessionRepository : JpaRepository<SessionToken, String> {
    fun findByToken(token: String): SessionToken?

    @Modifying
    @Transactional
    @Query("DELETE FROM SessionToken st WHERE st.usuario.id = :usuarioId")
    fun deleteUserSessionToken(@Param("usuarioId") usuarioId: UUID?): Int
}