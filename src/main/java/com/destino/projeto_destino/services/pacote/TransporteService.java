package com.destino.projeto_destino.services.pacote;

import com.destino.projeto_destino.dto.pacote.transporte.TransporteRegistroDTO;
import com.destino.projeto_destino.model.pacote.transporte.Transporte;
import com.destino.projeto_destino.repository.pacote.TransporteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransporteService {

    private final TransporteRepository transporteRepository;

    public TransporteService(TransporteRepository transporteRepository) {
        this.transporteRepository = transporteRepository;
    }

    public ResponseEntity<String> criarTransportes(TransporteRegistroDTO transporteRegistroDTO) {
        Transporte transporte = new Transporte();
        transporte.setEmpresa(transporteRegistroDTO.empresa());
        transporte.setMeio(transporteRegistroDTO.meio());
        transporte.setPreco(transporte.getPreco());

        Transporte transporteCriado = transporteRepository.save(transporte);
        return ResponseEntity.ok().body("Transporte por meio " + transporteCriado.getMeio() + " em nome da empresa " + transporteCriado.getEmpresa() + " criado!");
    }

    public ResponseEntity<List<Transporte>> pegarTransportes() {
        List<Transporte> transporteList = transporteRepository.findAll();
        return ResponseEntity.ok().body(transporteList);
    }
}
