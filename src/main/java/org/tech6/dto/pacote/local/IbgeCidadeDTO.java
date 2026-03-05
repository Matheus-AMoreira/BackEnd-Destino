package org.tech6.dto.pacote.local;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IbgeCidadeDTO(Long id, String nome) {}
