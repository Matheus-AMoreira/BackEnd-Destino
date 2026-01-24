package com.fatec.destino.services.usuario

import com.fatec.destino.dto.avaliacao.AvaliacaoRegistroDTO
import com.fatec.destino.model.Avaliacao
import com.fatec.destino.model.Compra
import com.fatec.destino.repository.pacote.PacoteRepository
import com.fatec.destino.repository.usuario.AvaliacaoRepository
import com.fatec.destino.repository.usuario.CompraRepository
import com.fatec.destino.repository.usuario.UsuarioRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
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
        val usuario = usuarioRepository.findByIdOrNull(dto.usuarioId)
            ?: throw RuntimeException("Usuário não encontrado")

        val pacote = pacoteRepository.findByIdOrNull(dto.pacoteId)
            ?: throw RuntimeException("Pacote não encontrado")

        // 1. Verifica se o usuário comprou o pacote
        val comprou = compraRepository.findAllByUsuarioId(usuario.id)?.any {
                compra -> compra?.pacote?.id == pacote.id
        } ?: false

        if (!comprou) {
            throw RuntimeException("Você não pode avaliar um pacote que não comprou.")
        }

        // 3. Verifica duplicidade (criação)
        if (avaliacaoRepository.findByUsuarioAndPacote(usuario, pacote)?.isPresent == true) {
            throw RuntimeException("Você já avaliou este pacote. Use a função de editar.")
        }

        val avaliacao = Avaliacao(
            usuario = usuario,
            pacote = pacote,
            nota = validarNota(dto.nota),
            comentario = dto.comentario,
            data = Date()
        )

        return avaliacaoRepository.save(avaliacao)
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
        avaliacao.data = Date()

        return avaliacaoRepository.save(avaliacao)!!
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
        return nota.coerceIn(1, 5)
    }
}