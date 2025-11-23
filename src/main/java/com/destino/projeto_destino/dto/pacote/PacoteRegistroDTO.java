package com.destino.projeto_destino.dto.pacote;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

public record PacoteRegistroDTO(
        String nome,
        String descricao,
        ArrayList<String> tags,
        BigDecimal preco,
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate inicio,
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate fim,
        int disponibilidade,
        int transporte,
        int hotel,
        UUID funcionario,
        int fotosDoPacote
) {
}
