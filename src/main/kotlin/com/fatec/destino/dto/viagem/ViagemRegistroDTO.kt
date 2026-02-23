package com.fatec.destino.dto.viagem

import java.time.LocalDate

data class ViagemRegistroDTO(
    val pacoteId: Long,
    val inicio: LocalDate,
    val fim: LocalDate,
    val disponibilidade: Int
)
