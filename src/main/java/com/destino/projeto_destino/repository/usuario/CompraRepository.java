package com.destino.projeto_destino.repository.usuario;

import com.destino.projeto_destino.model.Compra;
import com.destino.projeto_destino.model.pacote.Pacote;
import com.destino.projeto_destino.model.usuario.Usuario;
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
    @Query("SELECT MONTH(c.dataCompra), COUNT(c) FROM Compra c GROUP BY MONTH(c.dataCompra)")
    List<Object[]> countComprasByMonth();

    @Query("SELECT c FROM Compra c WHERE c.usuario.id = :usuarioId ORDER BY c.pacote.inicio DESC")
    List<Compra> findAllByUsuarioId(@Param("usuarioId") UUID usuarioId);

    // Busca compra específica garantindo que pertence ao usuário (segurança)
    @Query("SELECT c FROM Compra c WHERE c.id = :compraId AND c.usuario.email = :emailUsuario")
    Compra findByIdAndUsuarioEmail(@Param("compraId") Long compraId, @Param("emailUsuario") String emailUsuario);

    // Em CompraRepository.java
    Optional<Compra> findByUsuarioAndPacote(Usuario usuario, Pacote pacote);
}
