package com.fatec.destino.dto.local

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class IbgeEstadoDTO(
    var id: Long,
    var sigla: String,
    var nome: String,
    var regiao: IbgeRegiaoDTO
)
