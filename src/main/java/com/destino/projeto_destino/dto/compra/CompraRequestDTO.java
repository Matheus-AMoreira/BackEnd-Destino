package com.destino.projeto_destino.dto.compra;

import com.destino.projeto_destino.util.model.compra.Metodo;
import com.destino.projeto_destino.util.model.compra.Processador;

import java.util.UUID;

public record CompraRequestDTO(
        UUID usuarioId,
        long pacoteId,
        Metodo metodo,          // VISTA, PARCELADO
        Processador processador, // VISA, MASTERCARD, PIX
        int parcelas            // 1 a 12
) {
}
