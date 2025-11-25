package com.destino.projeto_destino.repository.pacote.hotel.local;

import com.destino.projeto_destino.model.pacote.hotel.cidade.estado.Estado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstadoRepository extends JpaRepository<Estado, Long> {
    List<Estado> findByRegiaoId(Long regiaoId);
}
