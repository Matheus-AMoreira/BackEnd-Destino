package com.fatec.destino.repository.pacote;

import com.fatec.destino.model.pacote.pacoteFoto.PacoteFoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PacoteFotoRepository extends JpaRepository<PacoteFoto, Long> {
}
