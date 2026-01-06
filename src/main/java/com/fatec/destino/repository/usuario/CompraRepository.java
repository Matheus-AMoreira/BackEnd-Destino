package com.fatec.destino.repository.usuario;

import com.fatec.destino.model.Compra;
import com.fatec.destino.model.pacote.Pacote;
import com.fatec.destino.model.usuario.Usuario;
import com.fatec.destino.util.model.pacote.PacoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

    // Agrupa compras por Mês. Retorna [Mês (Integer), Quantidade (Long)]
    @Query("SELECT MONTH(c.dataCompra), COUNT(c) FROM Compra c WHERE YEAR(c.dataCompra) = :ano GROUP BY MONTH(c.dataCompra) ORDER BY MONTH(c.dataCompra) ASC")
    List<Object[]> countComprasByMonth(@Param("ano") int ano);

    @Query("SELECT c FROM Compra c WHERE c.usuario.id = :usuarioId ORDER BY c.pacote.inicio DESC")
    List<Compra> findAllByUsuarioId(@Param("usuarioId") UUID usuarioId);

    @Query("SELECT c FROM Compra c WHERE c.usuario.id = :usuarioId AND c.pacote.status = :status ORDER BY c.pacote.inicio DESC")
    List<Compra> findAllByUsuarioIdWhereStatusConcluido(@Param("usuarioId") UUID usuarioId, @Param("status") PacoteStatus status);

    @Query("SELECT c FROM Compra c WHERE c.usuario.id = :usuarioId AND c.pacote.status = :status ORDER BY c.pacote.inicio DESC")
    List<Compra> findAllByUsuarioIdWhereStatusEmAdamento(@Param("usuarioId") UUID usuarioId, @Param("status") PacoteStatus status);

    // Busca compra específica garantindo que pertence ao usuario (segurança)
    @Query("SELECT c FROM Compra c WHERE c.id = :compraId AND c.usuario.email = :emailUsuario")
    Compra findByIdAndUsuarioEmail(@Param("compraId") Long compraId, @Param("emailUsuario") String emailUsuario);

    // Em CompraRepository.java
    Optional<Compra> findByUsuarioAndPacote(Usuario usuario, Pacote pacote);
}
