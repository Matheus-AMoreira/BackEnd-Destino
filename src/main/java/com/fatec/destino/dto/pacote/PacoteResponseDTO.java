package com.fatec.destino.dto.pacote;

import com.fatec.destino.model.pacote.hotel.Hotel;
import com.fatec.destino.model.pacote.pacoteFoto.PacoteFoto;
import com.fatec.destino.model.pacote.transporte.Transporte;
import com.fatec.destino.util.model.pacote.PacoteStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PacoteResponseDTO(
        long id,
        String nome,
        String descricao,
        List<String> tags,
        BigDecimal preco,
        LocalDate inicio,
        LocalDate fim,
        int disponibilidade,
        PacoteStatus status,
        Hotel hotel,
        Transporte transporte,
        PacoteFoto fotosDoPacote
) {
}
