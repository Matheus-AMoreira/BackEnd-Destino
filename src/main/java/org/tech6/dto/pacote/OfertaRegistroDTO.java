package org.tech6.dto.pacote;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OfertaRegistroDTO(
    BigDecimal preco,
    LocalDate inicio,
    LocalDate fim,
    int disponibilidade,
    long pacoteId,
    long transporteId,
    long hotelId
) {
}
