package com.fatec.destino.services.pacote

import com.fatec.destino.dto.pacote.PacoteRegistroDTO
import com.fatec.destino.dto.pacote.PacoteResponseDTO
import com.fatec.destino.model.pacote.Pacote
import com.fatec.destino.model.pacote.pacoteFoto.PacoteFoto
import com.fatec.destino.model.pacote.transporte.Transporte
import com.fatec.destino.repository.pacote.PacoteFotoRepository.PacoteFotoRepository
import com.fatec.destino.repository.pacote.PacoteRepository
import com.fatec.destino.repository.pacote.hotel.HotelRepository
import com.fatec.destino.repository.pacote.transporte.TransporteRepository
import com.fatec.destino.repository.usuario.UsuarioRepository
import com.fatec.destino.util.model.pacote.PacoteStatus
import jakarta.transaction.Transactional
import lombok.AllArgsConstructor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.function.Function
import java.util.function.Supplier
import java.util.stream.Collectors

@Service
class PacoteService(
    private val pacoteRepository: PacoteRepository,
    private val hotelRepository: HotelRepository,
    private val transporteRepository: TransporteRepository,
    private val usuarioRepository: UsuarioRepository,
    private val pacoteFotoRepository: PacoteFotoRepository
) {

    fun pegarPacotesAgrupadosPorLocal(): ResponseEntity<Map<String, List<Pacote>>> {
        val todosPacotes = pacoteRepository.findAll()

        // Em Kotlin usamos agrupamento nativo de coleções, muito mais simples que Collectors
        val agrupado = todosPacotes.groupBy { p ->
            "${p.hotel.cidade?.nome} - ${p.hotel.cidade?.estado?.sigla}"
        }

        return ResponseEntity.ok(agrupado)
    }

    fun pegarPacotes(pageable: Pageable): Page<PacoteResponseDTO> {
        val pacotes = pacoteRepository.encontrePacotes(pageable)
        return pacotes.map { it.toResponseDTO() }
    }

    fun buscarPacotesComFiltros(nome: String?, precoMax: BigDecimal?, pageable: Pageable): Page<PacoteResponseDTO> {
        val termo = if (!nome.isNullOrBlank()) nome else null

        return pacoteRepository.buscarComFiltros(termo, precoMax, pageable)
            .map { it.toResponseDTO() }
    }

    fun pegarPacotePorNomeExato(nome: String): Pacote? {
        return pacoteRepository.findByNome(nome)
    }

    fun pacotesMaisvendidos(): List<PacoteResponseDTO> {
        return pacoteRepository.procuraPacotesMaisVendidos()
    }

    @Transactional
    fun salvarOuAtualizar(dto: PacoteRegistroDTO, id: Long? = null): ResponseEntity<String> {
        val pacote = id?.let {
            pacoteRepository.findById(it).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
        } ?: Pacote()

        // O Service foca na infraestrutura (buscar no banco)
        val hotel = hotelRepository.findById(dto.hotel).orElseThrow { ... }
        val transporte = transporteRepository.findById(dto.transporte).orElseThrow { ... }
        val funcionario = usuarioRepository.findById(dto.funcionario).orElseThrow { ... }
        val foto = pacoteFotoRepository.findById(dto.pacoteFoto).orElse(null)

        try {
            // O Pacote garante a própria regra de negócio
            pacote.atualizarDados(dto, hotel, transporte, funcionario, foto)
            pacoteRepository.save(pacote)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body(e.message)
        }

        return ResponseEntity.ok("Pacote processado com sucesso!")
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
        id, nome, descricao, tags, preco, inicio, fim, disponibilidade, status, hotel, transporte, fotosDoPacote
    )
}