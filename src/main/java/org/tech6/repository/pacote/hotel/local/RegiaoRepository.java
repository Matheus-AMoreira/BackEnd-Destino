package org.tech6.repository.pacote.hotel.local;

import org.tech6.model.pacote.hotel.cidade.estado.regiao.Regiao;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RegiaoRepository implements PanacheRepository<Regiao> {
}
