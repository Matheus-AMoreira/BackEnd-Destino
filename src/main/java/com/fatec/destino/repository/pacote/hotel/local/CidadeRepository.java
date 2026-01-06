package com.fatec.destino.repository.pacote.hotel.local;

import com.fatec.destino.model.pacote.hotel.cidade.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CidadeRepository extends JpaRepository<Cidade, Long> {
    List<Cidade> findByEstadoId(Long estadoId);

    @Query("SELECT c FROM Cidade c WHERE c.estado.regiao.nome = :regiaoNome")
    List<Cidade> findByEstadoRegiaoNome(String regiaoNome);
}
