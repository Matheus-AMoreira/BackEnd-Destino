package com.destino.projeto_destino.repository.local;

import com.destino.projeto_destino.model.pacote.hotel.cidade.estado.regiao.Regiao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegiaoRepository extends JpaRepository<Regiao, Long> {
}
