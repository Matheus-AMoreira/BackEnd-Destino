package com.destino.projeto_destino.controller.pacote;

import com.destino.projeto_destino.dto.pacote.PacoteRegistroDTO;
import com.destino.projeto_destino.dto.pacote.TopDestinoDTO;
import com.destino.projeto_destino.model.pacote.Pacote;
import com.destino.projeto_destino.services.pacote.PacoteService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pacote")
@EnableMethodSecurity(prePostEnabled = true)
public class PacoteController {

    private final PacoteService pacoteService;

    public PacoteController(PacoteService pacoteService) {
        this.pacoteService = pacoteService;
    }

    // Suporta paginação
    // Exemplo: /api/pacote?page=0&size=10
    @GetMapping
    public ResponseEntity<Page<Pacote>> getPacotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return pacoteService.pegarPacotesPaginados(page, size);
    }

    @GetMapping("/publico")
    public ResponseEntity<List<Pacote>> getPacotesPublicos() {
        return pacoteService.pegarPacotesPublicos();
    }

    @GetMapping("/agrupado-admin")
    public ResponseEntity<Map<String, List<Pacote>>> getPacotesAdmin() {
        return pacoteService.pegarPacotesAgrupadosPorLocal();
    }

    // Top Destinos (baseado em vendas)
    @GetMapping("/destinos-populares")
    public ResponseEntity<List<TopDestinoDTO>> getDestinosPopulares() {
        // Retorna os top 10
        return pacoteService.pegarDestinosMaisVendidos(10);
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