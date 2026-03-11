package org.tech6.dto.pacote.local;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IbgeRegiaoDTO(Long id, String sigla, String nome) {}
