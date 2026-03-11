package org.tech6.repository.pacote.hotel.local;

import org.tech6.model.pacote.hotel.cidade.Cidade;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class CidadeRepository implements PanacheRepository<Cidade> {
    public List<Cidade> findByEstadoId(Long estadoId) {
        return find("estado.id", estadoId).list();
    }

    public List<Cidade> findByEstadoRegiaoNome(String regiaoNome) {
        return find("estado.regiao.nome", regiaoNome).list();
    }
}
