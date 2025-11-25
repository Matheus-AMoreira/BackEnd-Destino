package com.destino.projeto_destino.repository.pacote;

import com.destino.projeto_destino.dto.dashboard.ViagensResponseDTO;
import com.destino.projeto_destino.model.pacote.Pacote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PacoteRepository extends JpaRepository<Pacote, Integer> {

    List<Pacote> findByNome(String nome);

    @Query("SELECT p.id, p.nome, p.descricao, p.tags, p.preco FROM Pacote p")
    List<ViagensResponseDTO> buscarViagens();

    // --- Métodos de Verificação de Integridade ---
    boolean existsByHotelId(int id);

    boolean existsByTransporteId(int id);

    // --- Dashboard ---
    @Query("SELECT p.status, COUNT(p) FROM Pacote p GROUP BY p.status")
    List<Object[]> countPacotesByStatus();

    @Query("SELECT t.meio, COUNT(p) FROM Pacote p JOIN p.transporte t GROUP BY t.meio")
    List<Object[]> countPacotesByTransporteMeio();

    @Query("SELECT MONTH(p.inicio), COUNT(p) FROM Pacote p WHERE p.status = 'CONCLUIDO' GROUP BY MONTH(p.inicio)")
    List<Object[]> countViagensConcluidasByMonth();
}
