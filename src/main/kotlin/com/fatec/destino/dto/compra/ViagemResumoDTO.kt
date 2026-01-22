package com.fatec.destino.dto.compra

import com.fatec.destino.model.pacote.hotel.cidade.Cidade
import com.fatec.destino.util.model.compra.StatusCompra
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class ViagemResumoDTO(
    val id: UUID?,
    val pacoteId: Long?,
    val nomePacote: String?,
    val descricao: String?,
    val valor: BigDecimal?,
    val statusCompra: StatusCompra?,
    val dataPartida: LocalDate?,
    val dataRetorno: LocalDate?,
    val imagemCapa: String?,
    val cidade: Cidade?,
    val estado: String?
)
