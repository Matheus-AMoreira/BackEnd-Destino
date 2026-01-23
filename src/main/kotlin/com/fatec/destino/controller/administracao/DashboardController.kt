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
    @get:GetMapping("/status-viagem")
    val statusViagem: ResponseEntity<MutableList<ChartDataDTO?>?>
        get() = ResponseEntity.ok<MutableList<ChartDataDTO?>?>(dashboardService.getTransporteDistribution() as MutableList<ChartDataDTO?>?)

    @get:GetMapping("/transporte-stats")
    val transporteStats: ResponseEntity<MutableList<ChartDataDTO?>?>
        get() = ResponseEntity.ok<MutableList<ChartDataDTO?>?>(dashboardService.getTransporteDistribution() as MutableList<ChartDataDTO?>?)

    @GetMapping("/viagens/mensais")
    fun getViagensMensais(
        @RequestParam(name = "ano", required = false) ano: Int?
    ): ResponseEntity<MutableList<ChartDataDTO?>?> {
        // Se não vier ano na requisição, usa o ano atual do servidor
        val anoFiltro = if (ano != null) ano else LocalDate.now().getYear()
        return ResponseEntity.ok<MutableList<ChartDataDTO?>?>(dashboardService.getViagensConcluidasPorMes(anoFiltro) as MutableList<ChartDataDTO?>?)
    }

    @GetMapping("/viagens/vendidos")
    fun getComprasMensais(
        @RequestParam(name = "ano", required = false) ano: Int?
    ): ResponseEntity<MutableList<ChartDataDTO?>?> {
        val anoFiltro = if (ano != null) ano else LocalDate.now().getYear()
        return ResponseEntity.ok<MutableList<ChartDataDTO?>?>(dashboardService.getComprasPorMes(anoFiltro) as MutableList<ChartDataDTO?>?)
    }
}