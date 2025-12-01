package com.destino.projeto_destino.controller.administracao;

import com.destino.projeto_destino.dto.dashboard.ChartDataDTO;
import com.destino.projeto_destino.services.compra.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@EnableMethodSecurity(prePostEnabled = true)
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/status-viagem")
    public ResponseEntity<List<ChartDataDTO>> getStatusViagem() {
        return ResponseEntity.ok(dashboardService.getStatusDistribution());
    }

    @GetMapping("/transporte-stats")
    public ResponseEntity<List<ChartDataDTO>> getTransporteStats() {
        return ResponseEntity.ok(dashboardService.getTransporteDistribution());
    }

    @GetMapping("/viagens/mensais")
    public ResponseEntity<List<ChartDataDTO>> getViagensMensais() {
        return ResponseEntity.ok(dashboardService.getViagensConcluidasPorMes());
    }

    @GetMapping("/viagens/vendidos")
    public ResponseEntity<List<ChartDataDTO>> getComprasMensais() {
        return ResponseEntity.ok(dashboardService.getComprasPorMes());
    }
}
