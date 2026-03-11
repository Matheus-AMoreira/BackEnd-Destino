package org.tech6.repository.pacote.hotel.local;

import org.tech6.model.pacote.hotel.cidade.estado.Estado;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class EstadoRepository implements PanacheRepository<Estado> {
    public List<Estado> findByRegiaoId(Long regiaoId) {
        return find("regiao.id", regiaoId).list();
    }
}
