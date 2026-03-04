package com.fatec.destino.repository.pacote.hotel.local;

import com.fatec.destino.model.pacote.hotel.cidade.estado.regiao.Regiao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegiaoRepository extends JpaRepository<Regiao, Long> {
}
