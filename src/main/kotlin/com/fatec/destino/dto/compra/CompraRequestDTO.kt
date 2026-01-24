package com.fatec.destino.dto.compra

import com.fatec.destino.util.model.compra.Metodo
import com.fatec.destino.util.model.compra.Processador
import java.util.UUID

data class CompraRequestDTO(
    var usuarioId : UUID,
    var pacoteId: Long,
    var metodo: Metodo,
    var processador : Processador,
    var parcelas : Int
)
