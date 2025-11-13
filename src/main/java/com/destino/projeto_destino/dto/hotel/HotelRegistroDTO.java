package com.destino.projeto_destino.dto.hotel;

import com.destino.projeto_destino.model.local.Cidade;

import java.math.BigDecimal;

public record HotelRegistroDTO(
        String nome,
        String estado,
        Cidade cidade,
        String endereco,
        BigDecimal diaria
) {
}
