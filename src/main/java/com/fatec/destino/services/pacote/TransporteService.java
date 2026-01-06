package com.fatec.destino.services.pacote;

import com.fatec.destino.dto.pacote.transporte.TransporteRegistroDTO;
import com.fatec.destino.model.pacote.transporte.Transporte;
import com.fatec.destino.repository.pacote.PacoteRepository;
import com.fatec.destino.repository.pacote.TransporteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransporteService {

    private final TransporteRepository transporteRepository;
    private final PacoteRepository pacoteRepository;

    public TransporteService(TransporteRepository transporteRepository, PacoteRepository pacoteRepository) {
        this.transporteRepository = transporteRepository;
        this.pacoteRepository = pacoteRepository;
    }

    public ResponseEntity<String> criarTransportes(TransporteRegistroDTO dto) {
        Transporte transporte = new Transporte();
        transporte.setEmpresa(dto.empresa());
        transporte.setMeio(dto.meio());
        transporte.setPreco(dto.preco());
        Transporte criado = transporteRepository.save(transporte);
        return ResponseEntity.ok("Transporte " + criado.getEmpresa() + " criado!");
    }

    public ResponseEntity<String> atualizarTransporte(long id, TransporteRegistroDTO dto) {
        return transporteRepository.findById(id).map(t -> {
            t.setEmpresa(dto.empresa());
            t.setMeio(dto.meio());
            t.setPreco(dto.preco());
            transporteRepository.save(t);
            return ResponseEntity.ok("Transporte atualizado com sucesso!");
        }).orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<String> deletarTransporte(long id) {
        if (!transporteRepository.existsById(id)) return ResponseEntity.notFound().build();
        if (pacoteRepository.existsByTransporteId(id)) {
            return ResponseEntity.badRequest().body("Não é possível deletar: Existem pacotes vinculados.");
        }
        transporteRepository.deleteById(id);
        return ResponseEntity.ok("Transporte deletado com sucesso.");
    }

    public ResponseEntity<List<Transporte>> pegarTransportes() {
        return ResponseEntity.ok().body(transporteRepository.findAll());
    }
}
