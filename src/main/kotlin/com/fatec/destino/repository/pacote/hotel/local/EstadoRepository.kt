package com.fatec.destino.repository.pacote.hotel.local

import com.fatec.destino.model.pacote.hotel.cidade.estado.Estado
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EstadoRepository : JpaRepository<Estado, Long> {
    fun findByRegiaoId(regiaoId: Long?): MutableList<Estado?>?
}