package com.fatec.destino.services.pacote.PacoteFoto

import com.fatec.destino.dto.pacote.pacoteFoto.FotoDTO
import com.fatec.destino.dto.pacote.pacoteFoto.PacoteFotoRegistroDTO
import com.fatec.destino.model.pacote.pacoteFoto.PacoteFoto
import com.fatec.destino.model.pacote.pacoteFoto.foto.Foto
import com.fatec.destino.repository.pacote.PacoteFotoRepository.PacoteFotoRepository
import jakarta.transaction.Transactional
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class PacoteFotoService(
    private val pacoteFotoRepository: PacoteFotoRepository
) {

    fun listarPacotesFoto(): ResponseEntity<MutableList<PacoteFoto?>?> {
        return ResponseEntity.ok<T?>(pacoteFotoRepository.findAll())
    }

    fun buscarPorId(id: Long): ResponseEntity<PacoteFoto?> {
        return pacoteFotoRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build<T?>())
    }

    @Transactional
    fun criarPacoteFoto(dto: PacoteFotoRegistroDTO): ResponseEntity<String?> {
        val pacoteFoto = PacoteFoto()
        montarPacoteFoto(pacoteFoto, dto)
        pacoteFotoRepository.save(pacoteFoto)
        return ResponseEntity.ok<String?>("Pacote de fotos registrado com sucesso!")
    }

    private fun montarPacoteFoto(pacoteFoto: PacoteFoto, dto: PacoteFotoRegistroDTO) {
        pacoteFoto.nome = dto.nome
        pacoteFoto.fotoDoPacote = dto.url

        // Limpa fotos antigas se for edição (o orphanRemoval=true na entidade remove do banco)
        if (pacoteFoto.fotos != null) {
            pacoteFoto.fotos!!.clear()
        } else {
            pacoteFoto.fotos = HashSet<Foto?>()
        }

        // Adiciona as novas fotos
        if (dto.fotosAdicionais != null) {
            val fotosEntidade = dto.fotosAdicionais.stream().map<Foto?> { fDto: FotoDTO? ->
                val f = Foto()
                f.nome = fDto!!.nome
                f.url = fDto.url
                f
            }.collect(Collectors.toSet())

            pacoteFoto.fotos!!.addAll(fotosEntidade)
        }
    }

    @Transactional
    fun atualizarPacoteFoto(id: Long, dto: PacoteFotoRegistroDTO): ResponseEntity<String?> {
        return pacoteFotoRepository.findById(id).map({ pacoteFoto ->
            montarPacoteFoto(pacoteFoto, dto)
            pacoteFotoRepository.save(pacoteFoto)
            ResponseEntity.ok<T?>("Pacote de fotos atualizado com sucesso!")
        }).orElse(ResponseEntity.notFound().build<T?>())
    }
}