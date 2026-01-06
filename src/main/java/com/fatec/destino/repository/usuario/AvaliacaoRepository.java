package com.fatec.destino.repository.usuario;

import com.fatec.destino.model.Avaliacao;
import com.fatec.destino.model.pacote.Pacote;
import com.fatec.destino.model.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Integer> {
    // Busca para verificar se já existe avaliação deste usuário para este pacote
    Optional<Avaliacao> findByUsuarioAndPacote(Usuario usuario, Pacote pacote);
}
