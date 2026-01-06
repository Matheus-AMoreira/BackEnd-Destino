package com.fatec.destino.repository.pacote.hotel.local;

import com.fatec.destino.model.pacote.hotel.cidade.estado.Estado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstadoRepository extends JpaRepository<Estado, Long> {
    List<Estado> findByRegiaoId(Long regiaoId);
}
