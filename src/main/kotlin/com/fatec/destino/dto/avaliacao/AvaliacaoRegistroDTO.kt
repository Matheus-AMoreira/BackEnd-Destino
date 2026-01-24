package com.fatec.destino.dto.avaliacao

import java.util.UUID

data class AvaliacaoRegistroDTO(
    var usuarioId: UUID,
    var pacoteId: Long,
    var nota: Int,
    var comentario: String,
)
