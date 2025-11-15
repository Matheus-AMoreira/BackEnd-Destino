package com.destino.projeto_destino.dto.pacote;

import com.destino.projeto_destino.model.pacote.hotel.Hotel;
import com.destino.projeto_destino.model.pacote.pacoteFoco.PacoteFoto;
import com.destino.projeto_destino.model.pacote.transporte.Transporte;
import com.destino.projeto_destino.model.usuario.Usuario;
import com.destino.projeto_destino.util.pacote.Status;
import jakarta.validation.constraints.Future;

import java.util.Date;
import java.util.List;
import java.util.Set;

public record PacoteResponseDTO(
    String nome,

    String descricao,

    List<String> tags,

    int preco,

    @Future
    Date inicio,
    @Future
    Date fim,
    int disponibilidade,
    Status status,
    Transporte transporte,
    Set<PacoteFoto> fotosDoPacote,
    Usuario funcionarion,
    Hotel hotel
) {
}
