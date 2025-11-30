package com.destino.projeto_destino.controller.pacotes;

import com.destino.projeto_destino.dto.pacote.PacoteResponseDTO;
import com.destino.projeto_destino.model.pacote.Pacote;
import com.destino.projeto_destino.services.pacote.PacoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/publico/pacote")
public class PacotePublicoController {

    private final PacoteService pacoteService;

    public PacotePublicoController(PacoteService pacoteService) {
        this.pacoteService = pacoteService;
    }

    @GetMapping
    public ResponseEntity<List<PacoteResponseDTO>> getPacotes() {
        List<PacoteResponseDTO> pacotes = pacoteService.pegarPacotes();
        return ResponseEntity.ok().body(pacotes);
    }

    // Top Destinos (baseado em vendas)
    @GetMapping("/mais-vendidos")
    public ResponseEntity<List<PacoteResponseDTO>> getPacotesMaisVendidos() {
        List<PacoteResponseDTO> pacotes = pacoteService.pacotesMaisvendidos();
        return ResponseEntity.ok().body(pacotes);
    }

    @GetMapping("/buscar/{nome}")
    public ResponseEntity<List<PacoteResponseDTO>> getPacoteName(@PathVariable String nome) {
        String nomeDecodificado = URLDecoder.decode(nome, StandardCharsets.UTF_8);
        return ResponseEntity.ok().body(pacoteService.pegarPacotesPorNome(nomeDecodificado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pacote> getPacotePorId(@PathVariable long id) {
        return pacoteService.pegarPacotePorId(id);
    }
}
