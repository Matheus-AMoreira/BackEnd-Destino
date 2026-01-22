package com.fatec.destino.repository.usuario

import com.fatec.destino.model.usuario.Usuario
import com.fatec.destino.util.model.usuario.cpf.Cpf
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UsuarioRepository : JpaRepository<Usuario, UUID> {
    fun findByCpf(cpf: Cpf): Usuario?

    fun findByEmail(email: String): Usuario?

    fun findByValidoFalse(): MutableList<Usuario?>?

    @Modifying
    @Query("UPDATE Usuario u SET u.valido = true WHERE u.id = :id")
    fun validarUsuario(@Param("id") id: UUID): Int
}