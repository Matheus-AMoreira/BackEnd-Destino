package com.fatec.destino.repository.pacote.hotel

import com.fatec.destino.model.pacote.hotel.Hotel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface HotelRepository : JpaRepository<Hotel, Long> {
    fun findByCidadeId(cidadeId: Long?): MutableList<Hotel?>?

    @Query("SELECT h FROM Hotel h WHERE h.id = :id")
    fun findHotelById(@Param("id") id: Long): Hotel?

    @Query("SELECT h.diaria FROM Hotel h WHERE h.id = :id")
    fun findDiariaPorId(@Param("id") id: Long): Int

    @Query("SELECT h FROM Hotel h WHERE h.cidade.estado.id = :estadoId")
    fun findByEstadoId(estadoId: Long?): MutableList<Hotel?>?

    @Query("SELECT h FROM Hotel h WHERE h.cidade.estado.regiao.id = :regiaoId")
    fun findByRegiaoId(regiaoId: Long?): MutableList<Hotel?>?

    @Query("SELECT h FROM Hotel h WHERE h.cidade.estado.regiao.nome = :regiaoNome")
    fun findByCidadeEstadoRegiaoNome(regiao: String?): MutableList<Hotel?>?
}