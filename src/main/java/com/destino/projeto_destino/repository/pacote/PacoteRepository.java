package com.destino.projeto_destino.repository.pacote;

import com.destino.projeto_destino.dto.pacote.PacoteResponseDTO;
import com.destino.projeto_destino.model.pacote.Pacote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PacoteRepository extends JpaRepository<Pacote, Long> {

    @Query("SELECT p.id, p.nome, p.descricao, p.tags,p.preco, p.inicio,p.fim, " +
            "p.disponibilidade, p.status, p.hotel, p.transporte, p.fotosDoPacote from Pacote p")
    List<PacoteResponseDTO> encontrePacotes();

    @Query("SELECT p.id, p.nome, p.descricao, p.tags,p.preco, p.inicio,p.fim, " +
            "p.disponibilidade, p.status, p.hotel, p.transporte, p.fotosDoPacote " +
            "from Pacote p where p.nome = :nome")
    List<PacoteResponseDTO> procurePeloNome(String nome);

    // Busca as top cidades com base no número de compras
    @Query("SELECT c.pacote.id, c.pacote.nome, c.pacote.descricao, c.pacote.tags, c.pacote.preco, c.pacote.inicio, c.pacote.fim, " +
            "c.pacote.disponibilidade, c.pacote.status, c.pacote.hotel, c.pacote.transporte, c.pacote.fotosDoPacote FROM Compra c GROUP BY c.pacote ORDER BY COUNT(c.pacote) DESC")
    List<PacoteResponseDTO> procuraPacotesMaisVendidos();

    @Query("SELECT p FROM Pacote p WHERE p.status = 'EMANDAMENTO' OR p.status = 'CONCLUIDO' ORDER BY p.status DESC, p.inicio DESC")
    List<Pacote> findPacotesPublicos();

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
