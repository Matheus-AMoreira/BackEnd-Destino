package com.destino.projeto_destino.dto.pacote.local;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IbgeEstadoDTO(Long id, String sigla, String nome, IbgeRegiaoDTO regiao) {}
