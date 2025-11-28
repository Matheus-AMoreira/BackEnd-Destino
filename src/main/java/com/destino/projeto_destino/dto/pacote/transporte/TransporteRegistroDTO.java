package com.destino.projeto_destino.dto.pacote.transporte;

import com.destino.projeto_destino.util.model.transporte.Meio;

public record TransporteRegistroDTO(
        String empresa,
        Meio meio,
        int preco
) {
}
