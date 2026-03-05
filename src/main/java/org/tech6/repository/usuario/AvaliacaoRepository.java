package org.tech6.repository.usuario;

import org.tech6.model.Avaliacao;
import org.tech6.model.pacote.Pacote;
import org.tech6.model.usuario.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class AvaliacaoRepository implements PanacheRepository<Avaliacao> {
    // Busca para verificar se já existe avaliação deste usuário para este pacote
    public Optional<Avaliacao> findByUsuarioAndPacote(Usuario usuario, Pacote pacote) {
        return find("usuario = ?1 and pacote = ?2", usuario, pacote).firstResultOptional();
    }
}
