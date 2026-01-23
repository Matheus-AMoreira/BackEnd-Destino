package com.fatec.destino.dto.pacote

import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class PacoteRegistroDTO(
    val nome: String,
    val descricao: String,
    val tags: List<String>,
    val preco: Int,
    val inicio: LocalDate,
    val fim: LocalDate,
    val disponibilidade: Int,
    val transporte: Long,
    val hotel: Long,
    val pacoteFoto: Long,
    val funcionario : UUID
)
