package com.fatec.destino.services.dashboard

import com.fatec.destino.dto.dashboard.ChartDataDTO
import com.fatec.destino.repository.viagem.ViagemRepository
import com.fatec.destino.repository.usuario.CompraRepository
import org.springframework.stereotype.Service
import java.time.Month
import java.time.format.TextStyle
import java.util.*

@Service
class DashboardService(
    private val viagemRepository: ViagemRepository,
    private val compraRepository: CompraRepository
) {

    fun getStatusDistribution(): List<ChartDataDTO> {
        return viagemRepository.countViagensByStatus().map { result ->
            ChartDataDTO(result.status.exibicao, result.total)
        }
    }

    fun getTransporteDistribution(): List<ChartDataDTO> {
        return viagemRepository.countViagensByTransporteMeio().map { result ->
            val meio = result.meio
            val count = result.total

            ChartDataDTO(meio.name, count)
        }
    }

    fun getViagensConcluidasPorMes(ano: Int): List<ChartDataDTO> {
        return viagemRepository.countViagensConcluidasByMonth(ano).map { it ->
            ChartDataDTO(it.mes.toString(),it.total)
        }
    }

    fun getComprasPorMes(ano: Int): List<ChartDataDTO> {
        val locale = Locale.of("pt", "BR")

        return compraRepository.countComprasByMonth(ano).map { result ->
            val label = Month.of(result.mes)
                .getDisplayName(TextStyle.SHORT, locale)
                .replaceFirstChar { it.uppercase() }
                .replace(".", "")

            ChartDataDTO(label, result.total)
        }
    }
}