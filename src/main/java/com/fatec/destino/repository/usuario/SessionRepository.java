package com.fatec.destino.repository.usuario;

import com.fatec.destino.model.usuario.SessionToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<SessionToken, String> {

    Optional<SessionToken> findByToken(String token);

    @Modifying
    @Transactional
    @Query("DELETE FROM SessionToken st WHERE st.usuario.id = :usuarioId")
    int deleteUserSessionToken(@Param("usuarioId") UUID usuarioId);
}
