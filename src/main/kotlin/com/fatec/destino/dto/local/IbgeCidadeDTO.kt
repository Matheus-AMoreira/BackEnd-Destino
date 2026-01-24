package com.fatec.destino.dto.local

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class IbgeCidadeDTO(val id: Long, val nome: String)
