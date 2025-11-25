package com.destino.projeto_destino.controller.pacote;

import com.destino.projeto_destino.dto.dashboard.ViagensResponseDTO;
import com.destino.projeto_destino.dto.pacote.PacoteRegistroDTO;
import com.destino.projeto_destino.model.pacote.Pacote;
import com.destino.projeto_destino.services.pacote.PacoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pacote")
@EnableMethodSecurity(prePostEnabled = true)
public class PacoteController {

    private final PacoteService pacoteService;

    public PacoteController(PacoteService pacoteService) {
        this.pacoteService = pacoteService;
    }

    @GetMapping
    public ResponseEntity<List<Pacote>> getPacotes() {
        return pacoteService.pegarPacotes();
    }

    @GetMapping("/viagens")
    public ResponseEntity<List<ViagensResponseDTO>> getViagens() {
        List<ViagensResponseDTO> viagens = pacoteService.pegarViagens();
        return ResponseEntity.ok().body(viagens);
    }

    @GetMapping("/{name}")
    public ResponseEntity<List<Pacote>> getPacoteName(@PathVariable String nome) {
        return pacoteService.pegarPacotesPorNome(nome);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Pacote> getPacotePorId(@PathVariable int id) {
        return pacoteService.pegarPacotePorId(id);
    }

    @PostMapping()
    public ResponseEntity<String> registrarPacote(@RequestBody PacoteRegistroDTO pacoteRegistroDTO) {
        return pacoteService.criarPacote(pacoteRegistroDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizarPacote(@PathVariable int id, @RequestBody PacoteRegistroDTO pacoteRegistroDTO) {
        return pacoteService.atualizarPacote(id, pacoteRegistroDTO);
    }
}