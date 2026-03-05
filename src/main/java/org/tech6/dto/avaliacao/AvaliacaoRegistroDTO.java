package org.tech6.dto.avaliacao;

import java.util.UUID;

public record AvaliacaoRegistroDTO(
        UUID usuarioId,
        Long pacoteId,
        int nota,
        String comentario
) {}
