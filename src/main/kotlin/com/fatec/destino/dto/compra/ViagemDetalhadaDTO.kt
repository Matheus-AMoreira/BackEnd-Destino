package com.fatec.destino.dto.compra

import com.fatec.destino.dto.pacote.hotel.HotelRegistroDTO
import com.fatec.destino.model.pacote.pacoteFoto.foto.Foto
import com.fatec.destino.util.model.transporte.Meio
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

data class ViagemDetalhadaDTO(
    val id: UUID?,
    val nomePacote: String?,
    val descricao: String?,
    val valor: BigDecimal?,
    val statusCompra: String?,
    val dataPartida: LocalDate?,
    val dataRetorno: LocalDate?,
    val dataCompra: Date,
    val numeroReserva: String?,
    val imagemPrincipal: String?,
    val galeria: MutableSet<Foto?>?,
    val inclusoes: MutableList<String?>?,
    val nomeHotel: HotelRegistroDTO,
    val tipoTransporte: Meio?
)
