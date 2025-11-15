package com.destino.projeto_destino.repository;

import com.destino.projeto_destino.model.pacote.Pacote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PacoteRepository extends JpaRepository<Pacote, UUID> {

}
