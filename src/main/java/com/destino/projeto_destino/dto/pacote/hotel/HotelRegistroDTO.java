package com.destino.projeto_destino.dto.pacote.hotel;

import com.destino.projeto_destino.model.pacote.hotel.cidade.Cidade;

public record HotelRegistroDTO(
        String nome,
        String estado,
        Cidade cidade,
        String endereco,
        int diaria
) {
}
