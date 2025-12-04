package com.destino.projeto_destino.controller.administracao.pacote;

import com.destino.projeto_destino.dto.pacote.pacoteFoto.PacoteFotoRegistroDTO;
import com.destino.projeto_destino.model.pacote.pacoteFoto.PacoteFoto;
import com.destino.projeto_destino.services.pacote.PacoteFotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pacote-foto")
@PreAuthorize("hasRole('ROLE_FUNCIONARIO')")
public class PacoteFotoController {

    private final PacoteFotoService service;

    public PacoteFotoController(PacoteFotoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PacoteFoto>> listar() {
        return service.listarPacotesFoto();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacoteFoto> buscarPorId(@PathVariable long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<String> criar(@RequestBody PacoteFotoRegistroDTO dto) {
        return service.criarPacoteFoto(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable long id, @RequestBody PacoteFotoRegistroDTO dto) {
        return service.atualizarPacoteFoto(id, dto);
    }
}
