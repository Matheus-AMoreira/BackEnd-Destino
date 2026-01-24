package com.fatec.destino.dto.compra

import com.fatec.destino.model.Compra

data class CompraResponseDTO(
    var mensagem: String,
    var compra : Compra?
)
