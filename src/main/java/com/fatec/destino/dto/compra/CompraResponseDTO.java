package com.fatec.destino.dto.compra;

import com.fatec.destino.model.Compra;

import java.util.Optional;

public record CompraResponseDTO(
        String mensagem,
        Optional<Compra> compra
) {
}
