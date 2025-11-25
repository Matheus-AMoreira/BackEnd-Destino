package com.destino.projeto_destino.services;

import com.destino.projeto_destino.dto.dashboard.ChartDataDTO;
import com.destino.projeto_destino.repository.CompraRepository;
import com.destino.projeto_destino.repository.pacote.PacoteRepository;
import com.destino.projeto_destino.util.pacote.Status;
import com.destino.projeto_destino.util.transporte.Meio;
import org.springframework.stereotype.Service;

import java.text.DateFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final PacoteRepository pacoteRepository;
    private final CompraRepository compraRepository;

    public DashboardService(PacoteRepository pacoteRepository, CompraRepository compraRepository) {
        this.pacoteRepository = pacoteRepository;
        this.compraRepository = compraRepository;
    }

    public List<ChartDataDTO> getStatusDistribution() {
        return pacoteRepository.countPacotesByStatus().stream().map(result -> {
            Status status = (Status) result[0];
            Long count = (Long) result[1];

            String label = switch (status) {
                case CONCLUIDO -> "Concluídas";
                case EMANDAMENTO -> "Em Andamento";
                case CANCELADO -> "Canceladas";
            };
            return new ChartDataDTO(label, count);
        }).collect(Collectors.toList());
    }

    public List<ChartDataDTO> getTransporteDistribution() {
        return pacoteRepository.countPacotesByTransporteMeio().stream().map(result -> {
            Meio meio = (Meio) result[0];
            Long count = (Long) result[1];

            String label = switch (meio) {
                case AEREO -> "Aéreo";
                case MARITIMO -> "Marítimo";
                case TERRESTRE -> "Terrestre";
            };
            return new ChartDataDTO(label, count);
        }).collect(Collectors.toList());
    }

    public List<ChartDataDTO> getViagensConcluidasPorMes() {
        List<Object[]> results = pacoteRepository.countViagensConcluidasByMonth();
        return mapMonthResults(results);
    }

    private List<ChartDataDTO> mapMonthResults(List<Object[]> results) {
        // Pega os nomes curtos dos meses em Português do Brasil
        String[] meses = new DateFormatSymbols(new Locale("pt", "BR")).getShortMonths();

        return results.stream().map(result -> {
            Integer mesIndex = (Integer) result[0]; // Retorna 1 a 12
            Long count = (Long) result[1];

            // Ajuste de índice: array começa em 0, mas SQL MONTH retorna 1..12
            String label = (mesIndex != null && mesIndex >= 1 && mesIndex <= 12)
                    ? meses[mesIndex - 1]
                    : "Desc.";

            // Capitalize primeira letra (jan -> Jan)
            label = label.substring(0, 1).toUpperCase() + label.substring(1);

            return new ChartDataDTO(label, count);
        }).collect(Collectors.toList());
    }

    public List<ChartDataDTO> getComprasPorMes() {
        List<Object[]> results = compraRepository.countComprasByMonth();
        return mapMonthResults(results);
    }
}
