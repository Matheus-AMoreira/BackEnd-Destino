package com.destino.projeto_destino.controller;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@EnableMethodSecurity(prePostEnabled = true)
public class DashboardController {
    @GetMapping("/status/viagem")
    public int getStatusNumber() {
        return 1;
    }

    @GetMapping("/transporte")
    public int getTransporteNumber() {
        return 1;
    }

    @GetMapping("/viagens/mensais")
    public int getViagensMensais() {
        return 1;
    }

    @GetMapping("/viagens/vendidos")
    public int getViagensMaisVendidas() {
        return 1;
    }
}
