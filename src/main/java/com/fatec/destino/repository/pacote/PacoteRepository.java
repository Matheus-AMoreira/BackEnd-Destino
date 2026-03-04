package com.fatec.destino.repository.pacote;

import com.fatec.destino.dto.pacote.PacoteResponseDTO;
import com.fatec.destino.model.pacote.Pacote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PacoteRepository extends JpaRepository<Pacote, Long> {

        @Query("SELECT p from Pacote p JOIN p.ofertas o where o.status = com.fatec.destino.util.model.pacote.OfertaStatus.EMANDAMENTO")
        Page<Pacote> encontrePacotes(Pageable pageable);

        @Query("SELECT p from Pacote p where p.nome = :nome")
        List<Pacote> procurePeloNome(@Param("nome") String nome);

        // Busca os pacotes mais vendidos com base no número de compras
        @Query("SELECT c.oferta.pacote FROM Compra c GROUP BY c.oferta.pacote ORDER BY COUNT(c) DESC")
        List<Pacote> procuraPacotesMaisVendidos();

        @Query("SELECT p FROM Pacote p JOIN p.ofertas o WHERE o.status = 'EMANDAMENTO' OR o.status = 'CONCLUIDO' ORDER BY o.status DESC, o.inicio DESC")
        List<Pacote> findPacotesPublicos();

        @Query("SELECT p FROM Pacote p JOIN p.ofertas o WHERE o.status = 'EMANDAMENTO' " +
                        "AND (:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) " +
                        "AND (:precoMax IS NULL OR o.preco <= :precoMax)")
        Page<Pacote> buscarComFiltros(
                        @Param("nome") String nome,
                        @Param("precoMax") BigDecimal precoMax,
                        Pageable pageable);

        // Busca exata para a página de detalhes (URL amigável)
        Optional<Pacote> findByNome(String nome);

        // --- Métodos de Verificação de Integridade ---
        boolean existsByHotelId(long id);

        boolean existsByTransporteId(long id);

        // --- Dashboard ---
        @Query("SELECT o.status, COUNT(p) FROM Pacote p JOIN p.ofertas o GROUP BY o.status")
        List<Object[]> countPacotesByStatus();

        @Query("SELECT t.meio, COUNT(p) FROM Pacote p JOIN p.transporte t GROUP BY t.meio")
        List<Object[]> countPacotesByTransporteMeio();

        @Query("SELECT MONTH(o.inicio), COUNT(p) FROM Pacote p JOIN p.ofertas o WHERE o.status = 'CONCLUIDO' AND YEAR(o.inicio) = :ano GROUP BY MONTH(o.inicio) ORDER BY MONTH(o.inicio) ASC")
        List<Object[]> countViagensConcluidasByMonth(@Param("ano") int ano);
}
