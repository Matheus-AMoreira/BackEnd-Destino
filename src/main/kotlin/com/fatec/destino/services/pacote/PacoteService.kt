package com.fatec.destino.services.pacote

import com.fatec.destino.dto.pacote.PacoteRegistroDTO
import com.fatec.destino.dto.pacote.PacoteResponseDTO
import com.fatec.destino.model.pacote.Pacote
import com.fatec.destino.repository.pacote.pacoteFotoRepository.PacoteFotoRepository
import com.fatec.destino.repository.pacote.PacoteRepository
import com.fatec.destino.repository.pacote.hotel.HotelRepository
import com.fatec.destino.repository.pacote.transporte.TransporteRepository
import com.fatec.destino.repository.usuario.UsuarioRepository
import com.fatec.destino.model.pacote.tag.Tag
import com.fatec.destino.repository.pacote.tag.TagRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.math.BigDecimal
import kotlin.String

@Service
class PacoteService(
    private val pacoteRepository: PacoteRepository,
    private val hotelRepository: HotelRepository,
    private val transporteRepository: TransporteRepository,
    private val usuarioRepository: UsuarioRepository,
    private val pacoteFotoRepository: PacoteFotoRepository,
    private val tagRepository: TagRepository
) {

    fun pegarPacotesAgrupadosPorLocal(): ResponseEntity<Map<String, List<Pacote>>> {
        val todosPacotes = pacoteRepository.findAll()

        val agrupado = todosPacotes.groupBy { p ->
            "${p.hotel.cidade?.nome} - ${p.hotel.cidade?.estado?.sigla}"
        }

        return ResponseEntity.ok(agrupado)
    }

    fun pegarPacotes(pageable: Pageable): Page<PacoteResponseDTO> {
        return pacoteRepository.findAll(pageable).map { it.toResponseDTO() }
    }

    fun pegarPacotePorNomeExato(nome: String): Pacote? {
        return pacoteRepository.findByNome(nome)
    }

    fun pacotesMaisvendidos(): List<PacoteResponseDTO> {
        return pacoteRepository.procuraPacotesMaisVendidos()
    }

    @Transactional
    fun salvarOuAtualizar(dto: PacoteRegistroDTO, id: Long?): ResponseEntity<String> {
        val hotel = hotelRepository.findById(dto.hotel).orElseThrow { NoSuchElementException("Hotel não encontrado") }
        val transporte = transporteRepository.findById(dto.transporte).orElseThrow { NoSuchElementException("Transporte não encontrado") }
        val funcionario = usuarioRepository.findById(dto.funcionario).orElseThrow { NoSuchElementException("Funcionário não encontrado") }
        val foto = dto.pacoteFoto.let { pacoteFotoRepository.findById(it).orElse(null) }
        
        val tags = processarTags(dto.tags)

        val pacote = if (id != null) {
            pacoteRepository.findById(id).orElseThrow {
                NoSuchElementException("Pacote com ID $id não encontrado")
            }
        } else {
            Pacote(
                nome = dto.nome,
                descricao = dto.descricao,
                preco = dto.preco.toBigDecimal(),
                transporte = transporte,
                hotel = hotel,
                funcionario = funcionario,
                tags = tags
            )
        }

        if (id != null) {
            pacote.atualizarDados(dto, hotel, transporte, funcionario, foto, tags)
        }

        pacoteRepository.save(pacote)

        return ResponseEntity.ok("Pacote ${if (id == null) "criado" else "atualizado"} com sucesso!")
    }

    private fun processarTags(nomesTags: List<String>): Set<Tag> {
        return nomesTags.map { nome ->
            tagRepository.findByNome(nome) ?: tagRepository.save(Tag(nome))
        }.toSet()
    }

    fun pegarPacotePorId(id: Long): ResponseEntity<Pacote> {
        return pacoteRepository.findById(id)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }

    fun pegarPacotesPorNome(nome: String): List<PacoteResponseDTO> {
        return pacoteRepository.procurePeloNome(nome)
    }

    private fun Pacote.toResponseDTO() = PacoteResponseDTO(
        id, nome, preco.toInt(), hotel.cidade.nome, transporte.empresa.toString(), tags.map { it.nome }
    )

}