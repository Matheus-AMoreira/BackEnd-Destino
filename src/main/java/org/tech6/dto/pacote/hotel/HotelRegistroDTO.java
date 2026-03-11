package org.tech6.dto.pacote.hotel;

public record HotelRegistroDTO(
        String nome,
        String endereco,
        int diaria,
        Long cidade
) {
}
