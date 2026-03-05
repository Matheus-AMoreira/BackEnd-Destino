package org.tech6.repository.usuario;

import org.tech6.model.usuario.SessionToken;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class SessionRepository implements PanacheRepository<SessionToken> {

    public Optional<SessionToken> findByToken(String token) {
        return find("token", token).firstResultOptional();
    }

    @Transactional
    public int deleteUserSessionToken(UUID usuarioId) {
        return (int) delete("usuario.id = ?1", usuarioId);
    }
}
