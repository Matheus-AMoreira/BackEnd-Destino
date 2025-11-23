package com.destino.projeto_destino.controller.usuario;

import com.destino.projeto_destino.services.CompraService;
import com.destino.projeto_destino.services.usuario.AvaliacaoService;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/compra")
@EnableMethodSecurity(prePostEnabled = true)
public class CompraController {
    private final CompraService compraService;
    private final AvaliacaoService avaliacaoService;

    public CompraController(CompraService compraService, AvaliacaoService avaliacaoService) {
        this.compraService = compraService;
        this.avaliacaoService = avaliacaoService;
    }
}
