package org.tech6.dto.pacote;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

public record PacoteRegistroDTO(
                String nome,
                String descricao,
                ArrayList<String> tags,
        long pacoteFoto,
        UUID funcionario) {
}
