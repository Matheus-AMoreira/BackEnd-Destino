package com.destino.projeto_destino.controller.usuario;

import com.destino.projeto_destino.dto.compra.CompraRequestDTO;
import com.destino.projeto_destino.dto.compra.ViagemDetalhadaDTO;
import com.destino.projeto_destino.dto.compra.ViagemResumoDTO;
import com.destino.projeto_destino.services.compra.CompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/compra")
public class CompraController {

    private final CompraService compraService;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @PostMapping
    public ResponseEntity<String> realizarCompra(@RequestBody CompraRequestDTO dto) {
        return compraService.processarCompra(dto);
    }

    @GetMapping
    public ResponseEntity<List<ViagemResumoDTO>> listarMinhasViagens() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return ResponseEntity.ok(compraService.listarViagensDoUsuario(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ViagemDetalhadaDTO> detalharViagem(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return ResponseEntity.ok(compraService.buscarDetalhesViagem(id, email));
    }
}
