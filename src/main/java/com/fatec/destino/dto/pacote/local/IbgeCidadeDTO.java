package com.fatec.destino.dto.pacote.local;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IbgeCidadeDTO(Long id, String nome) {}
