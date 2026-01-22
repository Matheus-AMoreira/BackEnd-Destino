package com.fatec.destino.services.usuario

import com.fatec.destino.dto.avaliacao.AvaliacaoRegistroDTO
import com.fatec.destino.model.Avaliacao
import com.fatec.destino.model.Compra
import com.fatec.destino.repository.pacote.PacoteRepository
import com.fatec.destino.repository.usuario.AvaliacaoRepository
import com.fatec.destino.repository.usuario.CompraRepository
import com.fatec.destino.repository.usuario.UsuarioRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*
import java.util.function.Supplier

@Service
class AvaliacaoService(
    private val avaliacaoRepository: AvaliacaoRepository,
    private val usuarioRepository: UsuarioRepository,
    private val pacoteRepository: PacoteRepository,
    private val compraRepository: CompraRepository
) {
    @Transactional
    fun avaliarPacote(dto: AvaliacaoRegistroDTO): Avaliacao {
        val usuario = usuarioRepository.findById(dto.usuarioId)
            .orElseThrow<RuntimeException?>(Supplier { RuntimeException("Usuário não encontrado") })

        val pacote = pacoteRepository.findById(dto.pacoteId)
            .orElseThrow<RuntimeException?>(Supplier { RuntimeException("Pacote não encontrado") })

        // 1. Verifica se o usuário comprou o pacote
        val comprou = compraRepository.findAllByUsuarioId(usuario.id)!!.stream()
            .anyMatch { compra: Compra? -> compra?.pacote?.id === pacote.id }

        if (!comprou) {
            throw RuntimeException("Você não pode avaliar um pacote que não comprou.")
        }

        // 2. Verifica se a viagem já aconteceu (Opcional, mas recomendado)
        if (pacote.fim.isAfter(LocalDate.now())) {
            // throw new RuntimeException("Você só pode avaliar após o término da viagem.");
        }

        // 3. Verifica duplicidade (criação)
        if (avaliacaoRepository.findByUsuarioAndPacote(usuario, pacote)!!.isPresent()) {
            throw RuntimeException("Você já avaliou este pacote. Use a função de editar.")
        }

        val avaliacao = Avaliacao(
            usuario = usuario,
            pacote = pacote,
            nota = validarNota(dto.nota),
            comentario = dto.comentario,
            data = Date()
        )

        return avaliacaoRepository.save<Avaliacao?>(avaliacao)!!
    }

    @Transactional
    fun editarAvaliacao(avaliacaoId: Int, dto: AvaliacaoRegistroDTO): Avaliacao {
        val avaliacao = avaliacaoRepository.findById(avaliacaoId)
            .orElseThrow<RuntimeException?>(Supplier { RuntimeException("Avaliação não encontrada") })

        // Verifica se o usuário da edição é o dono da avaliação
        if (avaliacao.usuario.id != dto.usuarioId) {
            throw RuntimeException("Permissão negada para editar esta avaliação.")
        }

        avaliacao.nota = validarNota(dto.nota)
        avaliacao.comentario = dto.comentario
        avaliacao.data = Date() // Atualiza data para a da edição

        return avaliacaoRepository.save<Avaliacao?>(avaliacao)!!
    }

    @Transactional
    fun excluirAvaliacao(avaliacaoId: Int, usuarioId: UUID?) {
        val avaliacao = avaliacaoRepository.findById(avaliacaoId)
            .orElseThrow<RuntimeException?>(Supplier { RuntimeException("Avaliação não encontrada") })

        if (avaliacao.usuario.id != usuarioId) {
            throw RuntimeException("Permissão negada para excluir esta avaliação.")
        }

        avaliacaoRepository.delete(avaliacao)
    }

    private fun validarNota(nota: Int): Int {
        if (nota < 1) return 1
        if (nota > 5) return 5
        return nota
    }
}