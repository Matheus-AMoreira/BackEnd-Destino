package com.fatec.destino.services.pacote.hotel

import com.fatec.destino.dto.pacote.hotel.HotelRegistroDTO
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
import lombok.AllArgsConstructor
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
@AllArgsConstructor
class HotelService (
    private val hotelRepository: HotelRepository,
    private val cidadeRepository: CidadeRepository,
    private val estadoRepository: EstadoRepository,
    private val regiaoRepository: RegiaoRepository,
    private val pacoteRepository: PacoteRepository
) {

    // --- Métodos Auxiliares de Localização (Cascata) ---
    fun listarRegioes(): ResponseEntity<MutableList<Regiao?>?> {
        return ResponseEntity.ok<MutableList<Regiao?>?>(regiaoRepository.findAll())
    }

    fun listarEstadosPorRegiao(regiaoId: Long?): ResponseEntity<MutableList<Estado?>?> {
        return ResponseEntity.ok<MutableList<Estado?>?>(estadoRepository.findByRegiaoId(regiaoId))
    }

    fun listarCidadesPorEstado(estadoId: Long?): ResponseEntity<MutableList<Cidade?>?> {
        return ResponseEntity.ok<MutableList<Cidade?>?>(cidadeRepository.findByEstadoId(estadoId))
    }

    fun pegarHoteis(): MutableList<Hotel?> {
        return hotelRepository.findAll()
    }

    fun pegarHotelById(id: Long): Hotel? {
        return hotelRepository.findHotelById(id)
    }

    // --- CRUD Hotel ---
    @Transactional
    fun criarHotel(hotelRegistroDTO: HotelRegistroDTO): ResponseEntity<String?> {
        val cidadeReferencia = cidadeRepository.getReferenceById(
            hotelRegistroDTO.cidade
        )
        val hotelCriado: Hotel = hotelRepository.save<Hotel?>(Hotel(hotelRegistroDTO, cidadeReferencia))!!
        return ResponseEntity.ok().body<String?>("Hotel " + hotelCriado.nome + " criado!")
    }

    @Transactional
    fun atualizarHotel(id: Long, dto: HotelRegistroDTO): ResponseEntity<String?> {
        return hotelRepository.findById(id)
            .map<ResponseEntity<String?>?>(java.util.function.Function { hotel: Hotel? ->
                val cidade: Cidade = cidadeRepository.getReferenceById(dto.cidade)

                hotel?.nome = dto.nome
                hotel.endereco = dto.endereco
                hotel.diaria = dto.diaria
                hotel.cidade = cidade

                hotelRepository.save<Hotel>(hotel)
                ResponseEntity.ok<kotlin.String?>("Hotel atualizado com sucesso!")
            }).orElse(ResponseEntity.notFound().build<kotlin.String?>())!!
    }

    fun deletarHotel(id: Long): ResponseEntity<String?> {
        if (!hotelRepository.existsById(id)) {
            return ResponseEntity.notFound().build<String?>()
        }

        // Verifica se algum pacote usa este hotel
        if (pacoteRepository.existsByHotelId(id)) {
            return ResponseEntity.badRequest()
                .body<String?>("Não é possível deletar: Existem pacotes vinculados a este Hotel.")
        }

        hotelRepository.deleteById(id)
        return ResponseEntity.ok<String?>("Hotel deletado com sucesso.")
    }

    fun pegarTodasCidades(): ResponseEntity<MutableList<Cidade?>?> {
        return ResponseEntity.ok<MutableList<Cidade?>?>(cidadeRepository.findAll())
    }

    fun pegarHotelPorRegiao(regiao: String?): ResponseEntity<MutableList<Hotel?>?> {
        return ResponseEntity.ok().body<MutableList<Hotel?>?>(hotelRepository.findByCidadeEstadoRegiaoNome(regiao))
    }

    fun pegarCidadePorRegiao(regiao: String?): ResponseEntity<MutableList<Cidade?>?> {
        return ResponseEntity.ok().body<MutableList<Cidade?>?>(cidadeRepository.findByEstadoRegiaoNome(regiao))
    }
}