package com.destino.projeto_destino.dto.pacote;

import com.destino.projeto_destino.util.model.pacote.Status;

import java.util.Date;
import java.util.List;

public record PacoteResponseDTO(
        String nome,
        String descricao,
        List<String> tags,
        int preco,
        Date inicio,
        Date fim,
        int disponibilidade,
        Status status,
        int transporte,
        int fotosDoPacote,
        int hotel
) {
}
