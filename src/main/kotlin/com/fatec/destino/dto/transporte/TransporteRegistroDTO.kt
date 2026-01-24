package com.fatec.destino.dto.transporte

import com.fatec.destino.util.model.transporte.Meio


data class TransporteRegistroDTO(
    var empresa: String,
    var meio: Meio,
    var preco: Int
)
