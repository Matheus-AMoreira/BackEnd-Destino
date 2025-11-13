package com.destino.projeto_destino.dto.transporte;

import com.destino.projeto_destino.model.transporteUtils.Meio;

import java.math.BigDecimal;

public record TransporteRegistroDTO(
    String empresa,
    Meio meio,
    BigDecimal preco
) {
}
