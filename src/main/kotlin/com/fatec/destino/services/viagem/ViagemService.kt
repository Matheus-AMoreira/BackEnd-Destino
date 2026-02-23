package com.fatec.destino.services.viagem

import com.fatec.destino.dto.viagem.ViagemDTO
import com.fatec.destino.dto.viagem.ViagemRegistroDTO
import com.fatec.destino.model.viagem.Viagem
import com.fatec.destino.repository.pacote.PacoteRepository
import com.fatec.destino.repository.viagem.ViagemRepository
import com.fatec.destino.util.model.pacote.PacoteStatus
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class ViagemService(
    private val viagemRepository: ViagemRepository,
    private val pacoteRepository: PacoteRepository
) {

    fun listarViagensDisponiveis(pageable: Pageable): Page<ViagemDTO> {
        return viagemRepository.encontreViagens(pageable).map { it.toDTO() }
    }

    fun buscarViagensComFiltros(nome: String?, precoMax: BigDecimal?, pageable: Pageable): Page<ViagemDTO> {
        return viagemRepository.buscarComFiltros(nome, precoMax, pageable).map { it.toDTO() }
    }

    @Transactional
    fun agendarViagem(dto: ViagemRegistroDTO): ResponseEntity<String> {
        val pacote = pacoteRepository.findById(dto.pacoteId).orElseThrow {
            NoSuchElementException("Pacote não encontrado")
        }

        val viagem = Viagem(
            pacote = pacote,
            inicio = dto.inicio,
            fim = dto.fim,
            disponibilidade = dto.disponibilidade,
            status = PacoteStatus.EMANDAMENTO
        )

        viagemRepository.save(viagem)
        return ResponseEntity.ok("Viagem agendada com sucesso!")
    }

    @Transactional
    fun atualizarDatas(id: Long, dto: ViagemRegistroDTO): ResponseEntity<String> {
        val viagem = viagemRepository.findById(id).orElseThrow {
            NoSuchElementException("Viagem não encontrada")
        }

        viagem.inicio = dto.inicio
        viagem.fim = dto.fim
        viagem.disponibilidade = dto.disponibilidade
        viagem.recalcularStatus()

        viagemRepository.save(viagem)
        return ResponseEntity.ok("Datas da viagem atualizadas com sucesso!")
    }

    private fun Viagem.toDTO() = ViagemDTO(
        viagemId = id,
        pacoteId = pacote.id,
        nome = pacote.nome,
        descricao = pacote.descricao,
        valor = pacote.preco,
        inicio = inicio,
        fim = fim,
        imagemCapa = pacote.fotosDoPacote?.fotoDoPacote,
        cidade = pacote.hotel.cidade,
        estado = pacote.hotel.cidade.estado.sigla,
        hotel = pacote.hotel.nome,
        transporte = pacote.transporte.meio,
        tags = pacote.tags.map { it.nome }
    )
}
