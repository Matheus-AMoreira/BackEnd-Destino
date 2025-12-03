package com.destino.projeto_destino.dto.compra;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ViagemDetalhadaDTO(
        Long id,
        String nomePacote,
        String descricao,
        BigDecimal valor,
        String statusCompra,
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate dataPartida,
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate dataRetorno,
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate dataCompra,
        String numeroReserva,
        String imagemPrincipal,
        List<String> galeria,
        List<String> inclusoes,
        String nomeHotel,
        String tipoTransporte
) {}
