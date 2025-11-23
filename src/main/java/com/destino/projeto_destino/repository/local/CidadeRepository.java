package com.destino.projeto_destino.repository.local;

import com.destino.projeto_destino.model.pacote.hotel.cidade.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CidadeRepository extends JpaRepository<Cidade, Long> {
    List<Cidade> findByEstadoRegiaoNome(String regiao);
}
