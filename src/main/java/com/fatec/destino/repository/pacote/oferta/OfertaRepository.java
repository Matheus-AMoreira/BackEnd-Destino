package com.fatec.destino.repository.pacote.oferta;

import com.fatec.destino.model.pacote.oferta.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfertaRepository extends JpaRepository<Oferta, Long> {
    List<Oferta> findByPacoteId(long pacoteId);
}
