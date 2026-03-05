package org.tech6.dto.dashboard;

import java.math.BigDecimal;
import java.util.List;

public record ViagensResponseDTO(
        int id,
        String nome,
        String descricao,
        List<String> itens,
        BigDecimal valor
) {
}
