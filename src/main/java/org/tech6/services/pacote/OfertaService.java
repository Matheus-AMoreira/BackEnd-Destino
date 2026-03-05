package org.tech6.services.pacote;

import org.tech6.dto.pacote.OfertaRegistroDTO;
import org.tech6.model.pacote.Pacote;
import org.tech6.model.pacote.oferta.Oferta;
import org.tech6.repository.pacote.oferta.OfertaRepository;
import org.tech6.repository.pacote.PacoteRepository;
import org.tech6.util.model.pacote.OfertaStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class OfertaService {

    private final OfertaRepository ofertaRepository;
    private final PacoteRepository pacoteRepository;

    public OfertaService(OfertaRepository ofertaRepository, PacoteRepository pacoteRepository) {
        this.ofertaRepository = ofertaRepository;
        this.pacoteRepository = pacoteRepository;
    }

    @Transactional
    public String registrarOferta(OfertaRegistroDTO dto) {
        Pacote pacote = pacoteRepository.findById(dto.pacoteId());
        if (pacote == null) {
            throw new RuntimeException("Pacote não encontrado");
        }

        Oferta oferta = new Oferta();
        oferta.preco = dto.preco();
        oferta.inicio = dto.inicio();
        oferta.fim = dto.fim();
        oferta.disponibilidade = dto.disponibilidade();
        oferta.status = OfertaStatus.EMANDAMENTO;
        oferta.pacote = pacote;

        ofertaRepository.persist(oferta);

        return "Oferta registrada com sucesso!";
    }
}
