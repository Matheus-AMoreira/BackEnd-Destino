package org.tech6.repository.usuario;

import org.tech6.model.Compra;
import org.tech6.model.pacote.Pacote;
import org.tech6.model.usuario.Usuario;
import org.tech6.util.model.pacote.OfertaStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CompraRepository implements PanacheRepository<Compra> {

        // Agrupa compras por Mês. Retorna [Mês (Integer), Quantidade (Long)]
        public List<Object[]> countComprasByMonth(int ano) {
                return getEntityManager()
                                .createQuery("SELECT MONTH(c.dataCompra), COUNT(c) FROM Compra c WHERE YEAR(c.dataCompra) = :ano GROUP BY MONTH(c.dataCompra) ORDER BY MONTH(c.dataCompra) ASC",
                                                Object[].class)
                                .setParameter("ano", ano)
                                .getResultList();
        }

        public List<Compra> findAllByUsuarioId(UUID usuarioId) {
                return find("usuario.id = ?1 ORDER BY oferta.inicio DESC", usuarioId).list();
        }

        public List<Compra> findAllByUsuarioIdWhereStatusConcluido(UUID usuarioId, OfertaStatus status) {
                return find("usuario.id = ?1 AND oferta.status = ?2 ORDER BY oferta.inicio DESC", usuarioId, status)
                                .list();
        }

        public List<Compra> findAllByUsuarioIdWhereStatusEmAdamento(UUID usuarioId, OfertaStatus status) {
                return find("usuario.id = ?1 AND oferta.status = ?2 ORDER BY oferta.inicio DESC", usuarioId, status)
                                .list();
        }

        // Busca compra específica garantindo que pertence ao usuario (segurança)
        public Compra findByIdAndUsuarioEmail(Long compraId, String emailUsuario) {
                return find("id = ?1 AND usuario.email = ?2", compraId, emailUsuario).firstResult();
        }

        public Optional<Compra> findByUsuarioAndOferta(Usuario usuario,
                        org.tech6.model.pacote.oferta.Oferta oferta) {
                return find("usuario = ?1 AND oferta = ?2", usuario, oferta).firstResultOptional();
        }
}
