package com.fatec.destino.controller.administracao;

import com.fatec.destino.dto.dashboard.ChartDataDTO;
import com.fatec.destino.services.dashboard.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
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
    public ResponseEntity<List<ChartDataDTO>> getViagensMensais(
            @RequestParam(name = "ano", required = false) Integer ano
    ) {
        // Se não vier ano na requisição, usa o ano atual do servidor
        int anoFiltro = (ano != null) ? ano : LocalDate.now().getYear();
        return ResponseEntity.ok(dashboardService.getViagensConcluidasPorMes(anoFiltro));
    }

    @GetMapping("/viagens/vendidos")
    public ResponseEntity<List<ChartDataDTO>> getComprasMensais(
            @RequestParam(name = "ano", required = false) Integer ano
    ) {
        int anoFiltro = (ano != null) ? ano : LocalDate.now().getYear();
        return ResponseEntity.ok(dashboardService.getComprasPorMes(anoFiltro));
    }
}
