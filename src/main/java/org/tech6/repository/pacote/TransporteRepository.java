package org.tech6.repository.pacote;

import org.tech6.model.pacote.transporte.Transporte;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TransporteRepository implements PanacheRepository<Transporte> {
    public int findPrecoPorId(long id) {
        return find("id", id).project(Integer.class).firstResult();
    }
}
