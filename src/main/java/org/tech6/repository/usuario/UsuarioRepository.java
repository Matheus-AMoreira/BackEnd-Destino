package org.tech6.repository.usuario;

import org.tech6.model.usuario.Usuario;
import org.tech6.util.model.usuario.Cpf.Cpf;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UsuarioRepository implements PanacheRepositoryBase<Usuario, UUID> {
    public Optional<Usuario> findByCpf(Cpf cpf) {
        return find("cpf", cpf).firstResultOptional();
    }

    public Optional<Usuario> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public List<Usuario> findByValidoFalse() {
        return find("valido", false).list();
    }

    public int validarUsuario(UUID id) {
        return update("valido = true WHERE id = ?1", id);
    }
}
