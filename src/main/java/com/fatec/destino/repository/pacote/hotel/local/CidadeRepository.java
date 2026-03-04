package com.fatec.destino.repository.pacote.hotel.local;

import com.fatec.destino.model.pacote.hotel.cidade.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CidadeRepository extends JpaRepository<Cidade, Long> {
    List<Cidade> findByEstadoId(Long estadoId);

    @Query("SELECT c FROM Cidade c WHERE c.estado.regiao.nome = :regiaoNome")
    List<Cidade> findByEstadoRegiaoNome(@Param("regiaoNome") String regiaoNome);
}
