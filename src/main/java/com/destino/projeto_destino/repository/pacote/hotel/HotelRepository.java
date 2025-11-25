package com.destino.projeto_destino.repository.pacote.hotel;

import com.destino.projeto_destino.model.pacote.hotel.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {

    List<Hotel> findByCidadeId(Long cidadeId);

    @Query("SELECT h.diaria FROM Hotel h WHERE h.id = :id")
    int findDiariaPorId(@Param("id") Integer id);

    @Query("SELECT h FROM Hotel h WHERE h.cidade.estado.id = :estadoId")
    List<Hotel> findByEstadoId(Long estadoId);

    @Query("SELECT h FROM Hotel h WHERE h.cidade.estado.regiao.id = :regiaoId")
    List<Hotel> findByRegiaoId(Long regiaoId);

    @Query("SELECT h FROM Hotel h WHERE h.cidade.estado.regiao.nome = :regiaoNome")
    List<Hotel> findByCidadeEstadoRegiaoNome(String regiao);
}
