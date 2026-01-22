package com.fatec.destino.repository.usuario

import com.fatec.destino.model.Avaliacao
import com.fatec.destino.model.pacote.Pacote
import com.fatec.destino.model.usuario.Usuario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AvaliacaoRepository : JpaRepository<Avaliacao, Int> {
    fun findByUsuarioAndPacote(usuario: Usuario?, pacote: Pacote?): Optional<Avaliacao?>?
}