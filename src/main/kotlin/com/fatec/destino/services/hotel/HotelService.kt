package com.fatec.destino.services.hotel

import com.fatec.destino.dto.hotel.HotelRegistroDTO
import com.fatec.destino.model.pacote.hotel.Hotel
import com.fatec.destino.model.pacote.hotel.cidade.Cidade
import com.fatec.destino.model.pacote.hotel.cidade.estado.Estado
import com.fatec.destino.model.pacote.hotel.cidade.estado.regiao.Regiao
import com.fatec.destino.repository.pacote.PacoteRepository
import com.fatec.destino.repository.pacote.hotel.HotelRepository
import com.fatec.destino.repository.pacote.hotel.local.CidadeRepository
import com.fatec.destino.repository.pacote.hotel.local.EstadoRepository
import com.fatec.destino.repository.pacote.hotel.local.RegiaoRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class HotelService(
    private val hotelRepository: HotelRepository,
    private val cidadeRepository: CidadeRepository,
    private val estadoRepository: EstadoRepository,
    private val regiaoRepository: RegiaoRepository,
    private val pacoteRepository: PacoteRepository
) {

    // --- Métodos Auxiliares ---
    fun listarRegioes(): List<Regiao> = regiaoRepository.findAll()

    fun listarEstadosPorRegiao(regiaoId: Long): List<Estado?> =
        estadoRepository.findByRegiaoId(regiaoId) ?: emptyList()

    fun listarCidadesPorEstado(estadoId: Long): List<Cidade?> =
        cidadeRepository.findByEstadoId(estadoId) ?: emptyList()

    fun pegarHoteis(): List<Hotel> = hotelRepository.findAll()

    fun pegarHotelById(id: Long): Hotel? = hotelRepository.findHotelById(id)

    // --- CRUD Hotel ---
    @Transactional
    fun criarHotel(dto: HotelRegistroDTO): ResponseEntity<String> {
        val cidade = cidadeRepository.findByIdOrNull(dto.cidade)
            ?: return ResponseEntity.badRequest().body("Cidade não encontrada")

        val hotel = Hotel(
            nome = dto.nome,
            endereco = dto.endereco,
            diaria = dto.diaria,
            cidade = cidade
        )

        val salvo = hotelRepository.save(hotel)
        return ResponseEntity.ok("Hotel ${salvo.nome} criado!")
    }

    @Transactional
    fun atualizarHotel(id: Long, dto: HotelRegistroDTO): ResponseEntity<String> {
        val hotel = hotelRepository.findHotelById(id)
            ?: return ResponseEntity.notFound().build()

        val cidade = cidadeRepository.findByIdOrNull(dto.cidade)
            ?: return ResponseEntity.badRequest().body("Cidade não encontrada")

        hotel.nome = dto.nome
        hotel.endereco = dto.endereco
        hotel.diaria = dto.diaria
        hotel.cidade = cidade

        hotelRepository.save(hotel)
        return ResponseEntity.ok("Hotel atualizado com sucesso!")
    }

    fun deletarHotel(id: Long): ResponseEntity<String> {
        if (!hotelRepository.existsById(id)) {
            return ResponseEntity.notFound().build()
        }
        if (pacoteRepository.existsByHotelId(id)) {
            return ResponseEntity.badRequest().body("Não é possível deletar: Existem pacotes vinculados.")
        }
        hotelRepository.deleteById(id)
        return ResponseEntity.ok("Hotel deletado com sucesso.")
    }

    fun pegarTodasCidades(): List<Cidade> = cidadeRepository.findAll()

    fun pegarHotelPorRegiao(regiao: String): List<Hotel> =
        hotelRepository.findByCidadeEstadoRegiaoNome(regiao)

    fun pegarCidadePorRegiao(regiao: String): List<Cidade?> =
        cidadeRepository.findByEstadoRegiaoNome(regiao) ?: emptyList()
}