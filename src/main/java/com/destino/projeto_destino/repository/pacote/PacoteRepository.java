package com.destino.projeto_destino.repository.pacote;

import com.destino.projeto_destino.dto.dashboard.ViagensResponseDTO;
import com.destino.projeto_destino.dto.pacote.TopDestinoDTO;
import com.destino.projeto_destino.model.pacote.Pacote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PacoteRepository extends JpaRepository<Pacote, Integer> {

    Page<Pacote> findAll(Pageable pageable);

    List<Pacote> findByNome(String nome);

    @Query("SELECT p.id, p.nome, p.descricao, p.tags, p.preco FROM Pacote p")
    List<ViagensResponseDTO> buscarViagens();

    // --- QUERY PÚBLICA (CLIENTE) ---
    // Traz EM_ANDAMENTO (para comprar) ou CONCLUIDO (para ver histórico/portfólio)
    // Exclui CANCELADO
    // Ordena por data de início (mais recentes primeiro)
    @Query("SELECT p FROM Pacote p WHERE p.status = 'EMANDAMENTO' OR p.status = 'CONCLUIDO' ORDER BY p.status DESC, p.inicio DESC")
    List<Pacote> findPacotesPublicos();

    // Busca as top cidades com base no número de compras
    // Agrupa por Cidade e ordena pela contagem de compras decrescente
    @Query("SELECT c.id, c.nome, e.sigla, COUNT(com), MIN(pf.fotoDoPacote) " +
            "FROM Compra com " +
            "JOIN com.pacote p " +
            "JOIN p.hotel h " +
            "JOIN h.cidade c " +
            "JOIN c.estado e " +
            "LEFT JOIN p.fotosDoPacote pf " + // Pega a foto associada ao pacote
            "GROUP BY c.id, c.nome, e.sigla " +
            "ORDER BY COUNT(com) DESC")
    List<TopDestinoDTO> findDestinosMaisVendidos(Pageable pageable);

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
