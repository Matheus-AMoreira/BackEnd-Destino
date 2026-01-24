package com.fatec.destino.controller.administracao

import com.fatec.destino.dto.dashboard.ChartDataDTO
import com.fatec.destino.services.dashboard.DashboardService
import org.springframework.http.ResponseEntity
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/dashboard")
@EnableMethodSecurity(prePostEnabled = true)
class DashboardController(
    private val dashboardService: DashboardService
) {
    @GetMapping("/status-viagem")
    fun statusViagem(): ResponseEntity<List<ChartDataDTO>> {
        return ResponseEntity.ok(dashboardService.getStatusDistribution())
    }

    @GetMapping("/transporte-stats")
    fun transporteStats(): ResponseEntity<List<ChartDataDTO>> {
        return ResponseEntity.ok(dashboardService.getTransporteDistribution())
    }

    @GetMapping("/viagens/mensais")
    fun getViagensMensais(@RequestParam(name = "ano", required = false) ano: Int?): ResponseEntity<List<ChartDataDTO>> {
        val anoFiltro = ano ?: LocalDate.now().year
        return ResponseEntity.ok(dashboardService.getViagensConcluidasPorMes(anoFiltro))
    }

    @GetMapping("/viagens/vendidos")
    fun getComprasMensais(@RequestParam(name = "ano", required = false) ano: Int?): ResponseEntity<List<ChartDataDTO>> {
        val anoFiltro = ano ?: LocalDate.now().year
        return ResponseEntity.ok(dashboardService.getComprasPorMes(anoFiltro))
    }
}