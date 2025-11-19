package com.destino.projeto_destino.dto.pacote;

import com.destino.projeto_destino.util.pacote.Status;
import jakarta.validation.constraints.Future;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public record PacoteRegistroDTO(
        String nome,
        String descricao,
        ArrayList<String> tags,
        BigDecimal preco,
        @Future
        Date inicio,
        @Future
        Date fim,
        int disponibilidade,
        Status status,
        int transporte,
        int hotel,
        String funcionario,
        int fotosDoPacote

) {
}
