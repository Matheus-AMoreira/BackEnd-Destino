package com.destino.projeto_destino.repository.usuario;

import com.destino.projeto_destino.model.usuario.SessionToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<SessionToken, String> {

    Optional<SessionToken> findByToken(String token);

    @Modifying
    @Transactional
    @Query("DELETE FROM SessionToken st WHERE st.usuario.id = :usuarioId")
    int deleteUserSessionToken(@Param("usuarioId") String usuarioId);
}
