package com.destino.projeto_destino.dto.pacote;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

public record PacoteRegistroDTO(
        String nome,
        String descricao,
        ArrayList<String> tags,
        BigDecimal preco,
        LocalDate inicio,
        LocalDate fim,
        int disponibilidade,
        int transporte,
        int hotel,
        int pacoteFoto,
        UUID funcionario
) {
}
