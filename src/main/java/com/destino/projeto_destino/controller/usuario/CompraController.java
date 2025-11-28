package com.destino.projeto_destino.controller.usuario;

import com.destino.projeto_destino.dto.compra.CompraRequestDTO;
import com.destino.projeto_destino.services.CompraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
