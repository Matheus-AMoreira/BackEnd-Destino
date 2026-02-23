package com.fatec.destino.dto.viagem

import com.fatec.destino.model.pacote.hotel.cidade.Cidade
import com.fatec.destino.model.pacote.pacoteFoto.foto.Foto
import com.fatec.destino.util.model.compra.StatusCompra
import com.fatec.destino.util.model.transporte.Meio
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

data class ViagemDTO(
    val id: UUID? = null,
    val viagemId: Long? = null,
    val pacoteId: Long? = null,
    val nome: String? = null,
    val descricao: String? = null,
    val valor: BigDecimal? = null,
    val statusCompra: StatusCompra? = null,
    val inicio: LocalDate? = null,
    val fim: LocalDate? = null,
    val dataCompra: Date? = null,
    val numeroReserva: String? = null,
    val imagemCapa: String? = null,
    val galeria: List<Foto?>? = null,
    val tags: List<String>? = null,
    val hotel: String? = null,
    val cidade: Cidade? = null,
    val estado: String? = null,
    val transporte: Meio? = null
)
