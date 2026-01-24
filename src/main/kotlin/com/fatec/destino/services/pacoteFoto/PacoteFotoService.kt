package com.fatec.destino.services.pacoteFoto

import com.fatec.destino.dto.pacoteFoto.PacoteFotoRegistroDTO
import com.fatec.destino.model.pacote.pacoteFoto.PacoteFoto
import com.fatec.destino.model.pacote.pacoteFoto.foto.Foto
import com.fatec.destino.repository.pacote.pacoteFotoRepository.PacoteFotoRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class PacoteFotoService(
    private val pacoteFotoRepository: PacoteFotoRepository
) {

    fun listarPacotesFoto(): ResponseEntity<List<PacoteFoto>> {
        return ResponseEntity.ok(pacoteFotoRepository.findAll())
    }

    fun buscarPorId(id: Long): ResponseEntity<PacoteFoto> {
        val foto = pacoteFotoRepository.findByIdOrNull(id)
        return if (foto != null) ResponseEntity.ok(foto) else ResponseEntity.notFound().build()
    }

    @Transactional
    fun criarPacoteFoto(dto: PacoteFotoRegistroDTO): ResponseEntity<String> {
        val pacoteFoto = PacoteFoto(
            dto.nome,
            dto.url,
            dto.fotosAdicionais as HashSet<Foto?>
        )
        montarPacoteFoto(pacoteFoto, dto)
        pacoteFotoRepository.save(pacoteFoto)
        return ResponseEntity.ok("Pacote de fotos registrado com sucesso!")
    }

    @Transactional
    fun atualizarPacoteFoto(id: Long, dto: PacoteFotoRegistroDTO): ResponseEntity<String> {
        val pacoteFoto = pacoteFotoRepository.findByIdOrNull(id)
            ?: return ResponseEntity.notFound().build()

        montarPacoteFoto(pacoteFoto, dto)
        pacoteFotoRepository.save(pacoteFoto)
        return ResponseEntity.ok("Pacote de fotos atualizado com sucesso!")
    }

    private fun montarPacoteFoto(pacoteFoto: PacoteFoto, dto: PacoteFotoRegistroDTO) {
        pacoteFoto.nome = dto.nome
        pacoteFoto.fotoDoPacote = dto.url

        pacoteFoto.fotos.clear()

        dto.fotosAdicionais?.forEach { fDto ->
            val novaFoto = Foto().apply {
                nome = fDto.nome
                url = fDto.url
            }
            pacoteFoto.fotos.add(novaFoto)
        }
    }
}