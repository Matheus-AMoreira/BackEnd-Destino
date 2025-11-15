package com.destino.projeto_destino.repository;

import com.destino.projeto_destino.model.pacote.hotel.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Integer> {
}
