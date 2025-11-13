package com.destino.projeto_destino.repository.local;

import com.destino.projeto_destino.model.local.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CidadeRepository extends JpaRepository<Cidade, Long> {
}
