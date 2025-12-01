package com.destino.projeto_destino.dto.compra;

import com.destino.projeto_destino.model.Compra;

import java.util.Optional;

public record CompraResponseDTO(
        String mensagem,
        Optional<Compra> compra
) {
}
