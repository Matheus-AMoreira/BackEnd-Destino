package com.destino.projeto_destino.services.pacote;

import com.destino.projeto_destino.dto.pacote.pacoteFoto.PacoteFotoRegistroDTO;
import com.destino.projeto_destino.model.pacote.pacoteFoto.PacoteFoto;
import com.destino.projeto_destino.model.pacote.pacoteFoto.foto.Foto;
import com.destino.projeto_destino.repository.pacote.PacoteFotoRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PacoteFotoService {

    private final PacoteFotoRepository pacoteFotoRepository;

    public PacoteFotoService(PacoteFotoRepository pacoteFotoRepository) {
        this.pacoteFotoRepository = pacoteFotoRepository;
    }

    public ResponseEntity<List<PacoteFoto>> listarPacotesFoto() {
        return ResponseEntity.ok(pacoteFotoRepository.findAll());
    }

    public ResponseEntity<PacoteFoto> buscarPorId(long id) {
        return pacoteFotoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Transactional
    public ResponseEntity<String> criarPacoteFoto(PacoteFotoRegistroDTO dto) {
        PacoteFoto pacoteFoto = new PacoteFoto();
        montarPacoteFoto(pacoteFoto, dto);
        pacoteFotoRepository.save(pacoteFoto);
        return ResponseEntity.ok("Pacote de fotos registrado com sucesso!");
    }

    private void montarPacoteFoto(PacoteFoto pacoteFoto, PacoteFotoRegistroDTO dto) {
        pacoteFoto.setNome(dto.nome());
        pacoteFoto.setFotoDoPacote(dto.url());

        // Limpa fotos antigas se for edição (o orphanRemoval=true na entidade remove do banco)
        if (pacoteFoto.getFotos() != null) {
            pacoteFoto.getFotos().clear();
        } else {
            pacoteFoto.setFotos(new HashSet<>());
        }

        // Adiciona as novas fotos
        if (dto.fotosAdicionais() != null) {
            Set<Foto> fotosEntidade = dto.fotosAdicionais().stream().map(fDto -> {
                Foto f = new Foto();
                f.setNome(fDto.nome());
                f.setUrl(fDto.url());
                return f;
            }).collect(Collectors.toSet());

            pacoteFoto.getFotos().addAll(fotosEntidade);
        }
    }

    @Transactional
    public ResponseEntity<String> atualizarPacoteFoto(long id, PacoteFotoRegistroDTO dto) {
        return pacoteFotoRepository.findById(id).map(pacoteFoto -> {
            montarPacoteFoto(pacoteFoto, dto);
            pacoteFotoRepository.save(pacoteFoto);
            return ResponseEntity.ok("Pacote de fotos atualizado com sucesso!");
        }).orElse(ResponseEntity.notFound().build());
    }
}
