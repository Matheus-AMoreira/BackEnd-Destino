package com.destino.projeto_destino.dto.compra;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ViagemResumoDTO(
        Long id, // ID da Compra
        Long pacoteId,
        String nomePacote,
        String descricao,
        BigDecimal valor,
        String statusCompra, // CONFIRMADA, PENDENTE
        LocalDate dataPartida,
        LocalDate dataRetorno,
        String imagemCapa,
        String cidade,
        String estado
) {}
