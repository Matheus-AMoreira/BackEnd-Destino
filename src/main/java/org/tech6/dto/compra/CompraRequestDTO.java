package org.tech6.dto.compra;

import org.tech6.util.model.compra.Metodo;
import org.tech6.util.model.compra.Processador;

import java.util.UUID;

public record CompraRequestDTO(
                UUID usuarioId,
                long ofertaId,
                Metodo metodo, // VISTA, PARCELADO
                Processador processador, // VISA, MASTERCARD, PIX
                int parcelas // 1 a 12
) {
}
