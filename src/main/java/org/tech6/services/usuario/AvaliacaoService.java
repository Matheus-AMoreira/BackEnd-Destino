package org.tech6.services.usuario;

import org.tech6.dto.avaliacao.AvaliacaoRegistroDTO;
import org.tech6.model.Avaliacao;
import org.tech6.model.pacote.Pacote;
import org.tech6.model.usuario.Usuario;
import org.tech6.repository.pacote.PacoteRepository;
import org.tech6.repository.usuario.AvaliacaoRepository;
import org.tech6.repository.usuario.CompraRepository;
import org.tech6.repository.usuario.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.Date;
import java.util.UUID;

@ApplicationScoped
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PacoteRepository pacoteRepository;
    private final CompraRepository compraRepository;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository, UsuarioRepository usuarioRepository,
            PacoteRepository pacoteRepository, CompraRepository compraRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.pacoteRepository = pacoteRepository;
        this.compraRepository = compraRepository;
    }

    @Transactional
    public Avaliacao avaliarPacote(AvaliacaoRegistroDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.usuarioId());
        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado");
        }

        Pacote pacote = pacoteRepository.findById(dto.pacoteId());
        if (pacote == null) {
            throw new RuntimeException("Pacote não encontrado");
        }

        // 1. Verifica se o usuário comprou o pacote (através de qualquer uma de suas
        // ofertas)
        boolean comprou = compraRepository.findAllByUsuarioId(usuario.id).stream()
                .anyMatch(compra -> compra.oferta.pacote.id == pacote.id);

        if (!comprou) {
            throw new RuntimeException("Você não pode avaliar um pacote que não comprou.");
        }

        // 2. Verifica se a viagem já aconteceu (Opcional, mas recomendado)
        // Como o pacote pode ter várias ofertas, poderíamos verificar se existe pelo
        // menos uma oferta concluída comprada pelo usuário.
        // Simplificando por enquanto para não bloquear.

        // 3. Verifica duplicidade (criação)
        if (avaliacaoRepository.findByUsuarioAndPacote(usuario, pacote).isPresent()) {
            throw new RuntimeException("Você já avaliou este pacote. Use a função de editar.");
        }

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.usuario = usuario;
        avaliacao.pacote = pacote;
        avaliacao.nota = validarNota(dto.nota());
        avaliacao.comentario = dto.comentario();
        avaliacao.data = new Date();

        avaliacaoRepository.persist(avaliacao);
        return avaliacao;
    }

    @Transactional
    public Avaliacao editarAvaliacao(int avaliacaoId, AvaliacaoRegistroDTO dto) {
        Avaliacao avaliacao = avaliacaoRepository.findById((long) avaliacaoId);
        if (avaliacao == null) {
            throw new RuntimeException("Avaliação não encontrada");
        }

        // Verifica se o usuário da edição é o dono da avaliação
        if (!avaliacao.usuario.id.equals(dto.usuarioId())) {
            throw new RuntimeException("Permissão negada para editar esta avaliação.");
        }

        avaliacao.nota = validarNota(dto.nota());
        avaliacao.comentario = dto.comentario();
        avaliacao.data = new Date(); // Atualiza data para a da edição

        return avaliacao;
    }

    @Transactional
    public void excluirAvaliacao(int avaliacaoId, UUID usuarioId) {
        Avaliacao avaliacao = avaliacaoRepository.findById((long) avaliacaoId);
        if (avaliacao == null) {
            throw new RuntimeException("Avaliação não encontrada");
        }

        if (!avaliacao.usuario.id.equals(usuarioId)) {
            throw new RuntimeException("Permissão negada para excluir esta avaliação.");
        }

        avaliacaoRepository.delete(avaliacao);
    }

    private int validarNota(int nota) {
        if (nota < 1)
            return 1;
        if (nota > 5)
            return 5;
        return nota;
    }
}
