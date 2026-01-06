package com.fatec.destino.controller.administracao.pacote;

import com.fatec.destino.dto.pacote.transporte.TransporteRegistroDTO;
import com.fatec.destino.model.pacote.transporte.Transporte;
import com.fatec.destino.services.pacote.TransporteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transporte")
@EnableMethodSecurity(prePostEnabled = true)
public class TransporteController {
    private final TransporteService transporteService;

    public TransporteController(TransporteService transporteService) {
        this.transporteService = transporteService;
    }

    @GetMapping
    public ResponseEntity<List<Transporte>> getTransporte() {
        return transporteService.pegarTransportes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transporte> getTransporteById(@PathVariable int id) {
        return transporteService.pegarTransportes().getBody().stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<String> registrarTransporte(@RequestBody TransporteRegistroDTO hotel) {
        return transporteService.criarTransportes(hotel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizarTransporte(@PathVariable int id, @RequestBody TransporteRegistroDTO dto) {
        return transporteService.atualizarTransporte(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarTransporte(@PathVariable int id) {
        return transporteService.deletarTransporte(id);
    }
}
