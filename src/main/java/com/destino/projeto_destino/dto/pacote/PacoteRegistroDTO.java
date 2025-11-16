package com.destino.projeto_destino.dto.pacote;

import java.util.ArrayList;

public record PacoteRegistroDTO(
        String nome,
        String descricao,
        ArrayList<String> tags
) {
}
