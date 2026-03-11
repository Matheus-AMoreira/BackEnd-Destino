package org.tech6.repository.pacote;

import org.tech6.dto.pacote.PacoteResponseDTO;
import org.tech6.model.pacote.Pacote;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PacoteRepository implements PanacheRepository<Pacote> {

        public PanacheQuery<Pacote> encontrePacotes() {
                return find("SELECT p FROM Pacote p JOIN p.ofertas o WHERE o.status = org.tech6.util.model.pacote.OfertaStatus.EMANDAMENTO");
        }

        public List<Pacote> procurePeloNome(String nome) {
                return find("nome", nome).list();
        }

        // Busca os pacotes mais vendidos com base no número de compras
        public List<Pacote> procuraPacotesMaisVendidos() {
                return getEntityManager()
                                .createQuery("SELECT c.oferta.pacote FROM Compra c GROUP BY c.oferta.pacote ORDER BY COUNT(c) DESC",
                                                Pacote.class)
                                .getResultList();
        }

        public List<Pacote> findPacotesPublicos() {
                return find("SELECT p FROM Pacote p JOIN p.ofertas o WHERE o.status = 'EMANDAMENTO' OR o.status = 'CONCLUIDO' ORDER BY o.status DESC, o.inicio DESC")
                                .list();
        }

        public PanacheQuery<Pacote> buscarComFiltros(String nome, BigDecimal precoMax) {
                StringBuilder query = new StringBuilder(
                                "SELECT p FROM Pacote p JOIN p.ofertas o WHERE o.status = 'EMANDAMENTO' ");
                Parameters params = new Parameters();

                if (nome != null && !nome.isEmpty()) {
                        query.append("AND LOWER(p.nome) LIKE LOWER(:nome) ");
                        params.and("nome", "%" + nome + "%");
                }

                if (precoMax != null) {
                        query.append("AND o.preco <= :precoMax ");
                        params.and("precoMax", precoMax);
                }

                return find(query.toString(), params);
        }

        // Busca exata para a página de detalhes (URL amigável)
        public Optional<Pacote> findByNome(String nome) {
                return find("nome", nome).firstResultOptional();
        }

        // --- Métodos de Verificação de Integridade ---
        public boolean existsByHotelId(long id) {
                return count("from Pacote p join p.ofertas o where o.hotel.id = ?1", id) > 0;
        }

        public boolean existsByTransporteId(long id) {
                return count("from Pacote p join p.ofertas o where o.transporte.id = ?1", id) > 0;
        }

        // --- Dashboard ---
        public List<Object[]> countPacotesByStatus() {
                return getEntityManager()
                                .createQuery("SELECT o.status, COUNT(p) FROM Pacote p JOIN p.ofertas o GROUP BY o.status",
                                                Object[].class)
                                .getResultList();
        }

        public List<Object[]> countPacotesByTransporteMeio() {
                return getEntityManager()
                                .createQuery("SELECT t.meio, COUNT(p) FROM Pacote p JOIN p.ofertas o JOIN o.transporte t GROUP BY t.meio",
                                                Object[].class)
                                .getResultList();
        }

        public List<Object[]> countViagensConcluidasByMonth(int ano) {
                return getEntityManager()
                                .createQuery("SELECT MONTH(o.inicio), COUNT(p) FROM Pacote p JOIN p.ofertas o WHERE o.status = 'CONCLUIDO' AND YEAR(o.inicio) = :ano GROUP BY MONTH(o.inicio) ORDER BY MONTH(o.inicio) ASC",
                                                Object[].class)
                                .setParameter("ano", ano)
                                .getResultList();
        }
}
