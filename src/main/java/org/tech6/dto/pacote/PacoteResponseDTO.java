package org.tech6.dto.pacote;

import org.tech6.model.pacote.hotel.Hotel;
import org.tech6.model.pacote.pacoteFoto.PacoteFoto;
import org.tech6.model.pacote.tag.Tag;
import org.tech6.model.pacote.transporte.Transporte;
import org.tech6.util.model.pacote.OfertaStatus;

import org.tech6.model.pacote.oferta.Oferta;
import java.util.List;

public record PacoteResponseDTO(
                long id,
                String nome,
                String descricao,
                List<Tag> tags,
                List<Oferta> ofertas,
                Hotel hotel,
                Transporte transporte,
                PacoteFoto fotosDoPacote) {
}
