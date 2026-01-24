package com.fatec.destino.services.pacote

import com.fatec.destino.dto.pacote.PacoteRegistroDTO
import com.fatec.destino.dto.pacote.PacoteResponseDTO
import com.fatec.destino.model.pacote.Pacote
import com.fatec.destino.repository.pacote.pacoteFotoRepository.PacoteFotoRepository
import com.fatec.destino.repository.pacote.PacoteRepository
import com.fatec.destino.repository.pacote.hotel.HotelRepository
import com.fatec.destino.repository.pacote.transporte.TransporteRepository
import com.fatec.destino.repository.usuario.UsuarioRepository
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
    fun salvarOuAtualizar(dto: PacoteRegistroDTO, id: Long?): ResponseEntity<String> {
        // 1. Resolvemos a entidade (Ou buscamos no banco ou criamos uma nova)
        val pacote = if (id != null) {
            pacoteRepository.findById(id).orElseThrow {
                NoSuchElementException("Pacote com ID $id não encontrado")
            }
        } else {
            // Se o ID for nulo, instanciamos um pacote novo com dados iniciais obrigatórios
            // Aqui você precisará buscar as referências de Hotel, Transporte, etc., via ID do DTO
            Pacote(
                nome = dto.nome,
                descricao = dto.descricao,
                preco = dto.preco.toBigDecimal(),
                inicio = dto.inicio,
                fim = dto.fim,
                transporte = transporteRepository.findById(dto.transporte).get(),
                hotel = hotelRepository.findById(dto.hotel).get(),
                funcionario = usuarioRepository.findById(dto.funcionario).get()
            )
        }

        // 2. Se for uma atualização (ID != null), chamamos seu método de negócio
        if (id != null) {
            // Busque as dependências necessárias para o método atualizarDados
            val hotel = hotelRepository.findById(dto.hotel).get()
            val transporte = transporteRepository.findById(dto.transporte).get()
            val funcionario = usuarioRepository.findById(dto.funcionario).get()
            val foto = dto.pacoteFoto.let { pacoteFotoRepository.findById(it).orElse(null) }

            // Chama a lógica que você já criou dentro da Entity
            pacote.atualizarDados(dto, hotel, transporte, funcionario, foto)
        }

        // 3. Salva no banco.
        // Se o objeto 'pacote' veio do findById, ele já tem o ID preenchido e o Hibernate fará UPDATE.
        pacoteRepository.save(pacote)

        return ResponseEntity.ok("Pacote ${if (id == null) "criado" else "atualizado"} com sucesso!")
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
        id, nome, preco.toInt(), hotel.cidade.nome, transporte.empresa.toString()
    )
}