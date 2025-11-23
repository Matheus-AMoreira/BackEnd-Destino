package com.destino.projeto_destino.repository;

import com.destino.projeto_destino.model.pacote.hotel.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Integer> {
    List<Hotel> findByCidadeEstadoRegiaoNome(String regiao);

    @Query("SELECT h.diaria FROM Hotel h WHERE h.id = :id")
    int findDiariaPorId(@Param("id") Integer id);
}
