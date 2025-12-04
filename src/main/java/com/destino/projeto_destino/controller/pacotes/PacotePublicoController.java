package com.destino.projeto_destino.controller.pacotes;

import com.destino.projeto_destino.dto.pacote.PacoteResponseDTO;
import com.destino.projeto_destino.model.pacote.Pacote;
import com.destino.projeto_destino.services.pacote.PacoteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
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
    public ResponseEntity<Page<PacoteResponseDTO>> getPacotes(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) BigDecimal precoMax,
            @PageableDefault(page = 0, size = 12) Pageable pageable
    ) {
        return ResponseEntity.ok(pacoteService.buscarPacotesComFiltros(nome, precoMax, pageable));
    }

    // Top Destinos (baseado em vendas)
    @GetMapping("/mais-vendidos")
    public ResponseEntity<List<PacoteResponseDTO>> getPacotesMaisVendidos() {
        List<PacoteResponseDTO> pacotes = pacoteService.pacotesMaisvendidos();
        return ResponseEntity.ok().body(pacotes);
    }

    @GetMapping("/detalhes/{nome}")
    public ResponseEntity<Pacote> getPacotePorNomeUrl(@PathVariable String nome) {
        // Decodifica caso venha com caracteres especiais (espaço, acentos)
        String nomeDecodificado = URLDecoder.decode(nome, StandardCharsets.UTF_8);
        return pacoteService.pegarPacotePorNomeExato(nomeDecodificado);
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
