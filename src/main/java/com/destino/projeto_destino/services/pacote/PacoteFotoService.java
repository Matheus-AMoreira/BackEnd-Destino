package com.destino.projeto_destino.services.pacote;

import com.destino.projeto_destino.dto.pacote.pacoteFoto.PacoteFotoRegistroDTO;
import com.destino.projeto_destino.model.pacote.pacoteFoto.PacoteFoto;
import com.destino.projeto_destino.repository.pacote.PacoteFotoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacoteFotoService {

    private final PacoteFotoRepository pacoteFotoRepository;

    public PacoteFotoService(PacoteFotoRepository pacoteFotoRepository) {
        this.pacoteFotoRepository = pacoteFotoRepository;
    }

    public ResponseEntity<String> criarPacoteFoto(PacoteFotoRegistroDTO dto) {
        PacoteFoto pacoteFoto = new PacoteFoto();
        pacoteFoto.setNome(dto.nome());
        pacoteFoto.setFotoDoPacote(dto.url());

        pacoteFotoRepository.save(pacoteFoto);
        return ResponseEntity.ok("Pacote de fotos registrado com sucesso!");
    }

    public ResponseEntity<List<PacoteFoto>> listarPacotesFoto() {
        return ResponseEntity.ok(pacoteFotoRepository.findAll());
    }
}
