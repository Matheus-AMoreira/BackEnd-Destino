package com.fatec.destino.controller.administracao

import com.fatec.destino.dto.viagem.ViagemDTO
import com.fatec.destino.dto.viagem.ViagemRegistroDTO
import com.fatec.destino.services.viagem.ViagemService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/api/administracao/viagem")
class ViagemController(private val viagemService: ViagemService) {

    @GetMapping
    fun listarViagens(pageable: Pageable): Page<ViagemDTO> {
        return viagemService.listarViagensDisponiveis(pageable)
    }

    @GetMapping("/buscar")
    fun buscarViagens(
        @RequestParam(required = false) nome: String?,
        @RequestParam(required = false) precoMax: BigDecimal?,
        pageable: Pageable
    ): Page<ViagemDTO> {
        return viagemService.buscarViagensComFiltros(nome, precoMax, pageable)
    }

    @PostMapping
    fun agendarViagem(@RequestBody dto: ViagemRegistroDTO): ResponseEntity<String> {
        return viagemService.agendarViagem(dto)
    }

    @PutMapping("/{id}")
    fun atualizarDatas(@PathVariable id: Long, @RequestBody dto: ViagemRegistroDTO): ResponseEntity<String> {
        return viagemService.atualizarDatas(id, dto)
    }
}
