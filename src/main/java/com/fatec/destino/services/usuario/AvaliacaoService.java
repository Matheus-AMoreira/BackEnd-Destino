package com.fatec.destino.services.usuario;

import com.fatec.destino.dto.avaliacao.AvaliacaoRegistroDTO;
import com.fatec.destino.model.Avaliacao;
import com.fatec.destino.model.pacote.Pacote;
import com.fatec.destino.model.usuario.Usuario;
import com.fatec.destino.repository.pacote.PacoteRepository;
import com.fatec.destino.repository.usuario.AvaliacaoRepository;
import com.fatec.destino.repository.usuario.CompraRepository;
import com.fatec.destino.repository.usuario.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PacoteRepository pacoteRepository;
    private final CompraRepository compraRepository;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository, UsuarioRepository usuarioRepository, PacoteRepository pacoteRepository, CompraRepository compraRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.pacoteRepository = pacoteRepository;
        this.compraRepository = compraRepository;
    }

    @Transactional
    public Avaliacao avaliarPacote(AvaliacaoRegistroDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Pacote pacote = pacoteRepository.findById(dto.pacoteId())
                .orElseThrow(() -> new RuntimeException("Pacote não encontrado"));

        // 1. Verifica se o usuário comprou o pacote
        boolean comprou = compraRepository.findAllByUsuarioId(usuario.getId()).stream()
                .anyMatch(compra -> compra.getPacote().getId() == pacote.getId());

        if (!comprou) {
            throw new RuntimeException("Você não pode avaliar um pacote que não comprou.");
        }

        // 2. Verifica se a viagem já aconteceu (Opcional, mas recomendado)
        if (pacote.getFim().isAfter(LocalDate.now())) {
            // throw new RuntimeException("Você só pode avaliar após o término da viagem.");
        }

        // 3. Verifica duplicidade (criação)
        if (avaliacaoRepository.findByUsuarioAndPacote(usuario, pacote).isPresent()) {
            throw new RuntimeException("Você já avaliou este pacote. Use a função de editar.");
        }

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setUsuario(usuario);
        avaliacao.setPacote(pacote);
        avaliacao.setNota(validarNota(dto.nota()));
        avaliacao.setComentario(dto.comentario());
        avaliacao.setData(new Date());

        return avaliacaoRepository.save(avaliacao);
    }

    @Transactional
    public Avaliacao editarAvaliacao(int avaliacaoId, AvaliacaoRegistroDTO dto) {
        Avaliacao avaliacao = avaliacaoRepository.findById(avaliacaoId)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada"));

        // Verifica se o usuário da edição é o dono da avaliação
        if (!avaliacao.getUsuario().getId().equals(dto.usuarioId())) {
            throw new RuntimeException("Permissão negada para editar esta avaliação.");
        }

        avaliacao.setNota(validarNota(dto.nota()));
        avaliacao.setComentario(dto.comentario());
        avaliacao.setData(new Date()); // Atualiza data para a da edição

        return avaliacaoRepository.save(avaliacao);
    }

    @Transactional
    public void excluirAvaliacao(int avaliacaoId, UUID usuarioId) {
        Avaliacao avaliacao = avaliacaoRepository.findById(avaliacaoId)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada"));

        if (!avaliacao.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Permissão negada para excluir esta avaliação.");
        }

        avaliacaoRepository.delete(avaliacao);
    }

    private int validarNota(int nota) {
        if (nota < 1) return 1;
        if (nota > 5) return 5;
        return nota;
    }
}
