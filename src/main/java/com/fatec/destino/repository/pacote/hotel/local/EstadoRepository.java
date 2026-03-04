package com.fatec.destino.repository.pacote.hotel.local;

import com.fatec.destino.model.pacote.hotel.cidade.estado.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Long> {
    List<Estado> findByRegiaoId(Long regiaoId);
}
