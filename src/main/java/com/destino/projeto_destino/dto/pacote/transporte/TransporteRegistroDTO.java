package com.destino.projeto_destino.dto.pacote.transporte;

import com.destino.projeto_destino.util.transporte.Meio;

import java.math.BigDecimal;

public record TransporteRegistroDTO(
    String empresa,
    Meio meio,
    int preco
) {
}
