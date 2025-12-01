package com.destino.projeto_destino.dto.compra;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ViagemDetalhadaDTO(
        Long id,
        String nomePacote,
        String descricao,
        BigDecimal valor,
        String statusCompra,
        LocalDate dataPartida,
        LocalDate dataRetorno,
        LocalDate dataCompra,
        String numeroReserva,
        String imagemPrincipal,
        List<String> galeria,
        List<String> inclusoes,
        String nomeHotel,
        String tipoTransporte
) {}
