package com.fatec.destino.services.pacote;

import com.fatec.destino.dto.pacote.OfertaRegistroDTO;
import com.fatec.destino.model.pacote.Pacote;
import com.fatec.destino.model.pacote.oferta.Oferta;
import com.fatec.destino.repository.pacote.oferta.OfertaRepository;
import com.fatec.destino.repository.pacote.PacoteRepository;
import com.fatec.destino.util.model.pacote.OfertaStatus;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class OfertaService {

    private final OfertaRepository ofertaRepository;
    private final PacoteRepository pacoteRepository;

    @Transactional
    public ResponseEntity<String> registrarOferta(OfertaRegistroDTO dto) {
        Pacote pacote = pacoteRepository.findById(dto.pacoteId())
                .orElseThrow(() -> new RuntimeException("Pacote não encontrado"));

        Oferta oferta = new Oferta();
        oferta.setPreco(dto.preco());
        oferta.setInicio(dto.inicio());
        oferta.setFim(dto.fim());
        oferta.setDisponibilidade(dto.disponibilidade());
        oferta.setStatus(OfertaStatus.EMANDAMENTO);
        oferta.setPacote(pacote);

        ofertaRepository.save(oferta);

        return ResponseEntity.ok("Oferta registrada com sucesso!");
    }
}
