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

@RestController
@RequestMapping("/api/publico/pacote")
class PacotePublicoController(private val pacoteService: PacoteService) {
    @GetMapping
    fun getPacotes(
        @RequestParam(required = false) nome: String?,
        @RequestParam(required = false) precoMax: BigDecimal?,
        @PageableDefault(page = 0, size = 12) pageable: Pageable
    ): ResponseEntity<Page<PacoteResponseDTO?>?> {
        return ResponseEntity.ok<Page<PacoteResponseDTO?>?>(
            pacoteService.buscarPacotesComFiltros(
                nome,
                precoMax,
                pageable
            ) as Page<PacoteResponseDTO?>?
        )
    }

    @get:GetMapping("/mais-vendidos")
    val pacotesMaisVendidos: ResponseEntity<MutableList<PacoteResponseDTO?>?>
        // Top Destinos (baseado em vendas)
        get() {
            val pacotes: MutableList<PacoteResponseDTO?> = pacoteService.pacotesMaisvendidos() as MutableList<PacoteResponseDTO?>
            return ResponseEntity.ok().body<MutableList<PacoteResponseDTO?>?>(pacotes)
        }

    @GetMapping("/detalhes/{nome}")
    fun getPacotePorNomeUrl(@PathVariable nome: String): ResponseEntity<Pacote?>? {
        // Decodifica caso venha com caracteres especiais (espaço, acentos)
        val nomeDecodificado = URLDecoder.decode(nome, StandardCharsets.UTF_8)
        return pacoteService.pegarPacotePorNomeExato(nomeDecodificado) as ResponseEntity<Pacote?>?
    }

    @GetMapping("/buscar/{nome}")
    fun getPacoteName(@PathVariable nome: String): ResponseEntity<MutableList<PacoteResponseDTO?>?> {
        val nomeDecodificado = URLDecoder.decode(nome, StandardCharsets.UTF_8)
        return ResponseEntity.ok()
            .body<MutableList<PacoteResponseDTO?>?>(pacoteService.pegarPacotesPorNome(nomeDecodificado) as MutableList<PacoteResponseDTO?>?)
    }

    @GetMapping("/{id}")
    fun getPacotePorId(@PathVariable id: Long): ResponseEntity<Pacote?> {
        return pacoteService.pegarPacotePorId(id) as ResponseEntity<Pacote?>
    }
}