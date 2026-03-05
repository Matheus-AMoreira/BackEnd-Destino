package org.tech6.services.pacote;

import org.tech6.dto.pacote.pacoteFoto.PacoteFotoRegistroDTO;
import org.tech6.model.pacote.pacoteFoto.PacoteFoto;
import org.tech6.model.pacote.pacoteFoto.foto.Foto;
import org.tech6.repository.pacote.PacoteFotoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class PacoteFotoService {

    private final PacoteFotoRepository pacoteFotoRepository;

    public PacoteFotoService(PacoteFotoRepository pacoteFotoRepository) {
        this.pacoteFotoRepository = pacoteFotoRepository;
    }

    public List<PacoteFoto> listarPacotesFoto() {
        return pacoteFotoRepository.listAll();
    }

    public PacoteFoto buscarPorId(long id) {
        return pacoteFotoRepository.findById(id);
    }

    @Transactional
    public String criarPacoteFoto(PacoteFotoRegistroDTO dto) {
        PacoteFoto pacoteFoto = new PacoteFoto();
        montarPacoteFoto(pacoteFoto, dto);
        pacoteFotoRepository.persist(pacoteFoto);
        return "Pacote de fotos registrado com sucesso!";
    }

    private void montarPacoteFoto(PacoteFoto pacoteFoto, PacoteFotoRegistroDTO dto) {
        pacoteFoto.nome = dto.nome();
        pacoteFoto.fotoDoPacote = dto.url();

        // Limpa fotos antigas se for edição (o orphanRemoval=true na entidade remove do
        // banco)
        if (pacoteFoto.fotos != null) {
            pacoteFoto.fotos.clear();
        } else {
            pacoteFoto.fotos = new HashSet<>();
        }

        // Adiciona as novas fotos
        if (dto.fotosAdicionais() != null) {
            Set<Foto> fotosEntidade = dto.fotosAdicionais().stream().map(fDto -> {
                Foto f = new Foto();
                f.nome = fDto.nome();
                f.url = fDto.url();
                return f;
            }).collect(Collectors.toSet());

            pacoteFoto.fotos.addAll(fotosEntidade);
        }
    }

    @Transactional
    public String atualizarPacoteFoto(long id, PacoteFotoRegistroDTO dto) {
        PacoteFoto pacoteFoto = pacoteFotoRepository.findById(id);
        if (pacoteFoto == null) {
            return null; // Or throw an exception, depending on desired error handling
        }
        montarPacoteFoto(pacoteFoto, dto);
        // Panache entities are automatically managed within a transaction, no explicit
        // persist() needed for updates
        return "Pacote de fotos atualizado com sucesso!";
    }
}
