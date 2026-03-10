package org.tech6.dto.compra;

import org.tech6.model.Compra;

import java.util.Optional;

public record CompraResponseDTO(
        String mensagem,
        Compra compra
) {
}
