package com.destino.projeto_destino.repository.pacote;

import com.destino.projeto_destino.model.pacote.pacoteFoto.PacoteFoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PacoteFotoRepository extends JpaRepository<PacoteFoto, Integer> {
}
