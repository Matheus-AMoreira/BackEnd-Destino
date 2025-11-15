package com.destino.projeto_destino.repository.local;

import com.destino.projeto_destino.model.pacote.hotel.cidade.estado.Estado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstadoRepository extends JpaRepository<Estado, Long> {
}
