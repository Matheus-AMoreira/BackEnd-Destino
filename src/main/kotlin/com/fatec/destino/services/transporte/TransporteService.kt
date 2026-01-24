package com.fatec.destino.services.transporte

import com.fatec.destino.dto.transporte.TransporteRegistroDTO
import com.fatec.destino.model.pacote.transporte.Transporte
import com.fatec.destino.repository.pacote.PacoteRepository
import com.fatec.destino.repository.pacote.transporte.TransporteRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class TransporteService(
    private val transporteRepository: TransporteRepository,
    private val pacoteRepository: PacoteRepository
) {
    fun buscarPorId(id: Long): Transporte? {
       return transporteRepository.findByIdOrNull(id)
    }

    fun criarTransportes(dto: TransporteRegistroDTO): ResponseEntity<String> {
        val transporte = Transporte()
        transporte.empresa = dto.empresa
        transporte.meio = dto.meio
        transporte.preco = dto.preco
        val criado = transporteRepository.save(transporte)
        return ResponseEntity.ok("Transporte ${criado.empresa} criado!")
    }

    fun atualizarTransporte(id: Long, dto: TransporteRegistroDTO): ResponseEntity<String> {
        val transporte = transporteRepository.findByIdOrNull(id)
            ?: return ResponseEntity.notFound().build()

        transporte.empresa = dto.empresa
        transporte.meio = dto.meio
        transporte.preco = dto.preco
        transporteRepository.save(transporte)

        return ResponseEntity.ok("Transporte atualizado com sucesso!")
    }

    fun deletarTransporte(id: Long): ResponseEntity<String> {
        if (!transporteRepository.existsById(id)) return ResponseEntity.notFound().build()

        if (pacoteRepository.existsByTransporteId(id)) {
            return ResponseEntity.badRequest().body("Não é possível deletar: Existem pacotes vinculados.")
        }

        transporteRepository.deleteById(id)
        return ResponseEntity.ok("Transporte deletado com sucesso.")
    }

    fun pegarTransportes(): ResponseEntity<List<Transporte>> {
        return ResponseEntity.ok(transporteRepository.findAll())
    }
}