package com.fatec.destino.dto.dashboard

data class ViagensResponseDTO(
    var id: Int,
    var nome: String,
    var descricao: String,
    var itens: List<String>,
    var valor: Int
)
