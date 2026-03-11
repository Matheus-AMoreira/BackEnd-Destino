package org.tech6.services.pacote;

import org.tech6.dto.pacote.transporte.TransporteRegistroDTO;
import org.tech6.model.pacote.transporte.Transporte;
import org.tech6.repository.pacote.PacoteRepository;
import org.tech6.repository.pacote.TransporteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class TransporteService {

    private final TransporteRepository transporteRepository;
    private final PacoteRepository pacoteRepository;

    public TransporteService(TransporteRepository transporteRepository, PacoteRepository pacoteRepository) {
        this.transporteRepository = transporteRepository;
        this.pacoteRepository = pacoteRepository;
    }

    @Transactional
    public String criarTransportes(TransporteRegistroDTO dto) {
        Transporte transporte = new Transporte();
        transporte.empresa = dto.empresa();
        transporte.meio = dto.meio();
        transporte.preco = dto.preco();
        transporteRepository.persist(transporte);
        return "Transporte " + transporte.empresa + " criado!";
    }

    @Transactional
    public String atualizarTransporte(long id, TransporteRegistroDTO dto) {
        Transporte t = transporteRepository.findById(id);
        if (t == null)
            return null;

        t.empresa = dto.empresa();
        t.meio = dto.meio();
        t.preco = dto.preco();
        return "Transporte atualizado com sucesso!";
    }

    @Transactional
    public String deletarTransporte(long id) {
        Transporte t = transporteRepository.findById(id);
        if (t == null)
            return "Não encontrado";

        if (pacoteRepository.existsByTransporteId(id)) {
            return "Não é possível deletar: Existem pacotes vinculados.";
        }
        transporteRepository.delete(t);
        return "Transporte deletado com sucesso.";
    }

    public List<Transporte> pegarTransportes() {
        return transporteRepository.listAll();
    }

    public Transporte pegarTransportePorId(long id) {
        return transporteRepository.findById(id);
    }
}
