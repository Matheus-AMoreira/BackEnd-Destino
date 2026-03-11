package org.tech6.repository.pacote;

import org.tech6.model.pacote.pacoteFoto.PacoteFoto;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PacoteFotoRepository implements PanacheRepository<PacoteFoto> {
}
