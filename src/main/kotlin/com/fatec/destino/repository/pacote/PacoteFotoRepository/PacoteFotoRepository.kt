package com.fatec.destino.repository.pacote.PacoteFotoRepository

import com.fatec.destino.model.pacote.pacoteFoto.PacoteFoto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PacoteFotoRepository : JpaRepository<PacoteFoto, Long> {
}