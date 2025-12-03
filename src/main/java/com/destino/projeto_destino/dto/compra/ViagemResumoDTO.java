package com.destino.projeto_destino.dto.compra;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ViagemResumoDTO(
        Long id,
        Long pacoteId,
        String nomePacote,
        String descricao,
        BigDecimal valor,
        String statusCompra,
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate dataPartida,
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate dataRetorno,
        String imagemCapa,
        String cidade,
        String estado
) {}
