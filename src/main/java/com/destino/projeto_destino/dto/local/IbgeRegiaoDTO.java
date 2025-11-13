package com.destino.projeto_destino.dto.local;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IbgeRegiaoDTO(Long id, String sigla, String nome) {}
