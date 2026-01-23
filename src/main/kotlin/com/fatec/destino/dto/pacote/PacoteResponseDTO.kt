package com.fatec.destino.dto.pacote

data class PacoteResponseDTO(
    val id: Long?,
    val nome: String,
    val preco: Int,
    val hotel: String,
    val transporte: String,
)
