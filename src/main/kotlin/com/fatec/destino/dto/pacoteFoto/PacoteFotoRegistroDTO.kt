package com.fatec.destino.dto.pacoteFoto

data class PacoteFotoRegistroDTO(
    var nome: String,
    var url: String,
    var fotosAdicionais: List<FotoDTO>?
)
