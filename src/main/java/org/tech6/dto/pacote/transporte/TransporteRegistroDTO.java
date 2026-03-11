package org.tech6.dto.pacote.transporte;

import org.tech6.util.model.transporte.Meio;

public record TransporteRegistroDTO(
        String empresa,
        Meio meio,
        int preco
) {
}
