package com.fatec.destino.dto.pacote.transporte;

import com.fatec.destino.util.model.transporte.Meio;

public record TransporteRegistroDTO(
        String empresa,
        Meio meio,
        int preco
) {
}
