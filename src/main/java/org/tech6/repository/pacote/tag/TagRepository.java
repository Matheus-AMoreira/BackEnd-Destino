package org.tech6.repository.pacote.tag;

import org.tech6.model.pacote.tag.Tag;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class TagRepository implements PanacheRepository<Tag> {
    public Optional<Tag> findByNome(String nome) {
        return find("nome", nome).firstResultOptional();
    }
}
