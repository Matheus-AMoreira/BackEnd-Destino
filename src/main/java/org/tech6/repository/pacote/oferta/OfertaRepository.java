package org.tech6.repository.pacote.oferta;

import org.tech6.model.pacote.oferta.Oferta;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class OfertaRepository implements PanacheRepository<Oferta> {
    public List<Oferta> findByPacoteId(long pacoteId) {
        return find("pacote.id", pacoteId).list();
    }
}
