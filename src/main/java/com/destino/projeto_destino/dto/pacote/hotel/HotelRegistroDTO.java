package com.destino.projeto_destino.dto.pacote.hotel;

public record HotelRegistroDTO(
        String nome,
        String endereco,
        int diaria,
        Long cidade
) {
}
