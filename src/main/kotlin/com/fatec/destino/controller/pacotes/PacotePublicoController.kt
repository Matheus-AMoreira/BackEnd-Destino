package com.fatec.destino.controller.pacotes

import com.fatec.destino.dto.pacote.PacoteResponseDTO
import com.fatec.destino.model.pacote.Pacote
import com.fatec.destino.services.pacote.PacoteService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import kotlin.collections.emptyList

@RestController
@RequestMapping("/api/publico/pacote")
class PacotePublicoController(private val pacoteService: PacoteService) {

    @GetMapping
    fun getPacotes(
        @RequestParam(required = false) nome: String?,
        @RequestParam(required = false) precoMax: BigDecimal?,
        @PageableDefault(page = 0, size = 12) pageable: Pageable
    ): ResponseEntity<Page<PacoteResponseDTO>> { // Removido ? extras
        return ResponseEntity.ok(
            pacoteService.buscarPacotesComFiltros(nome, precoMax, pageable)
        )
    }

    @GetMapping("/mais-vendidos")
    fun pacotesMaisVendidos(): ResponseEntity<List<PacoteResponseDTO>> {
        return ResponseEntity.ok(pacoteService.pacotesMaisvendidos())
    }

    @GetMapping("/detalhes/{nome}")
    fun getPacotePorNomeUrl(@PathVariable nome: String): ResponseEntity<Pacote> {
        val nomeDecodificado = URLDecoder.decode(nome, StandardCharsets.UTF_8)
        val pacote = pacoteService.pegarPacotePorNomeExato(nomeDecodificado)
        return if (pacote != null) ResponseEntity.ok(pacote) else ResponseEntity.notFound().build()
    }

    @GetMapping("/buscar/{nome}")
    fun getPacoteName(@PathVariable nome: String): ResponseEntity<List<PacoteResponseDTO>> {
        val nomeDecodificado = URLDecoder.decode(nome, StandardCharsets.UTF_8)
        return ResponseEntity.ok(pacoteService.pegarPacotesPorNome(nomeDecodificado))
    }

    @GetMapping("/{id}")
    fun getPacotePorId(@PathVariable id: Long): ResponseEntity<Pacote> {
        return pacoteService.pegarPacotePorId(id)
    }
}