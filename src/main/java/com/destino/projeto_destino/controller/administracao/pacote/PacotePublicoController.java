package com.destino.projeto_destino.controller.administracao.pacote;

import com.destino.projeto_destino.dto.pacote.TopDestinoDTO;
import com.destino.projeto_destino.model.pacote.Pacote;
import com.destino.projeto_destino.services.pacote.PacoteService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/publico/pacote")
public class PacotePublicoController {

    private final PacoteService pacoteService;

    public PacotePublicoController(PacoteService pacoteService) {
        this.pacoteService = pacoteService;
    }

    @GetMapping("/agrupado-admin")
    public ResponseEntity<Map<String, List<Pacote>>> getPacotesAdmin() {
        return pacoteService.pegarPacotesAgrupadosPorLocal();
    }

    // Rotas Publicas

    // Suporta paginação
    // Exemplo: /api/publico/pacote?page=0&size=10
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
}
