package com.fatec.destino.dto.pacote;

import com.fatec.destino.model.pacote.hotel.Hotel;
import com.fatec.destino.model.pacote.pacoteFoto.PacoteFoto;
import com.fatec.destino.model.pacote.tag.Tag;
import com.fatec.destino.model.pacote.transporte.Transporte;
import com.fatec.destino.util.model.pacote.OfertaStatus;

import com.fatec.destino.model.pacote.oferta.Oferta;
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
