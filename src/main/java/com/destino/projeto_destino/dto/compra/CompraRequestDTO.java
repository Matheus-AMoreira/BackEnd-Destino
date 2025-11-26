package com.destino.projeto_destino.dto.compra;

import com.destino.projeto_destino.util.compra.Metodo;
import com.destino.projeto_destino.util.compra.Processador;

import java.util.UUID;

public record CompraRequestDTO(
        UUID usuarioId,
        int pacoteId,
        Metodo metodo,          // VISTA, PARCELADO
        Processador processador, // VISA, MASTERCARD, PIX
        int parcelas            // 1 a 12
) {
}
