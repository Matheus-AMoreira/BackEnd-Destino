package com.fatec.destino.services.pacote.Transporte

import com.fatec.destino.dto.pacote.transporte.TransporteRegistroDTO
import com.fatec.destino.model.pacote.transporte.Transporte
import com.fatec.destino.repository.pacote.PacoteRepository
import com.fatec.destino.repository.pacote.transporte.TransporteRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class TransporteService(
    private val transporteRepository: TransporteRepository,
    private val pacoteRepository: PacoteRepository
) {
    fun criarTransportes(dto: TransporteRegistroDTO): ResponseEntity<String?> {
        val transporte = Transporte()
        transporte.empresa = dto.empresa
        transporte.meio = dto.meio
        transporte.preco = dto.preco
        val criado: Transporte = transporteRepository.save(transporte)
        return ResponseEntity.ok<String?>("Transporte " + criado.empresa + " criado!")
    }

    fun atualizarTransporte(id: Long, dto: TransporteRegistroDTO): ResponseEntity<String?> {
        return transporteRepository.findById(id).map({ t ->
            t.setEmpresa(dto.empresa)
            t.setMeio(dto.meio)
            t.setPreco(dto.preco)
            transporteRepository.save(t)
            ResponseEntity.ok<T?>("Transporte atualizado com sucesso!")
        }).orElse(ResponseEntity.notFound().build<T?>())
    }

    fun deletarTransporte(id: Long): ResponseEntity<String?> {
        if (!transporteRepository.existsById(id)) return ResponseEntity.notFound().build<String?>()
        if (pacoteRepository.existsByTransporteId(id)) {
            return ResponseEntity.badRequest().body<String?>("Não é possível deletar: Existem pacotes vinculados.")
        }
        transporteRepository.deleteById(id)
        return ResponseEntity.ok<String?>("Transporte deletado com sucesso.")
    }

    fun pegarTransportes(): ResponseEntity<MutableList<Transporte?>?> {
        return ResponseEntity.ok().body<T?>(transporteRepository.findAll())
    }
}