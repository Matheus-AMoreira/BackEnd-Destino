package com.fatec.destino.controller.compra;

import com.fatec.destino.dto.compra.CompraRequestDTO;
import com.fatec.destino.dto.compra.CompraResponseDTO;
import com.fatec.destino.dto.compra.ViagemDetalhadaDTO;
import com.fatec.destino.dto.compra.ViagemResumoDTO;
import com.fatec.destino.services.compra.CompraService;
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

    @GetMapping("/andamento")
    public ResponseEntity<List<ViagemResumoDTO>> listarViagensEmAndamento() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return ResponseEntity.ok().body(compraService.listarViagensEmAndamentoDoUsuario(email));
    }

    @GetMapping("/concluidas")
    public ResponseEntity<List<ViagemResumoDTO>> listarViagensComcluidas() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return ResponseEntity.ok().body(compraService.listarViagensConcluidasDoUsuarios(email));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ViagemDetalhadaDTO> detalharViagem(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return ResponseEntity.ok().body(compraService.buscarDetalhesViagem(id, email));
    }

    @PostMapping
    public ResponseEntity<CompraResponseDTO> realizarCompra(@RequestBody CompraRequestDTO dto) {
        return ResponseEntity.ok().body(compraService.processarCompra(dto));
    }
}
