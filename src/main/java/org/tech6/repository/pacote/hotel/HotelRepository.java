package org.tech6.repository.pacote.hotel;

import org.tech6.model.pacote.hotel.Hotel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class HotelRepository implements PanacheRepository<Hotel> {

    public List<Hotel> findByCidadeId(Long cidadeId) {
        return find("cidade.id", cidadeId).list();
    }

    public Hotel findHotelById(long id) {
        return findById(id);
    }

    public int findDiariaPorId(long id) {
        return find("id", id).project(Integer.class).firstResult();
    }

    public List<Hotel> findByEstadoId(Long estadoId) {
        return find("cidade.estado.id", estadoId).list();
    }

    public List<Hotel> findByRegiaoId(Long regiaoId) {
        return find("cidade.estado.regiao.id", regiaoId).list();
    }

    public List<Hotel> findByCidadeEstadoRegiaoNome(String regiao) {
        return find("cidade.estado.regiao.nome", regiao).list();
    }
}
