package com.fatec.destino.controller.compra

import com.fatec.destino.dto.compra.CompraRequestDTO
import com.fatec.destino.dto.compra.CompraResponseDTO
import com.fatec.destino.dto.compra.ViagemDetalhadaDTO
import com.fatec.destino.dto.compra.ViagemResumoDTO
import com.fatec.destino.services.compra.CompraService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/compra")
class CompraController(private val compraService: CompraService) {
    @GetMapping("/andamento")
    fun listarViagensEmAndamento(): ResponseEntity<MutableList<ViagemResumoDTO?>?> {
        val auth = SecurityContextHolder.getContext().getAuthentication()
        val email = auth?.name
        return ResponseEntity.ok()
            .body<MutableList<ViagemResumoDTO?>?>(compraService.listarViagensEmAndamentoDoUsuario(email))
    }

    @GetMapping("/concluidas")
    fun listarViagensComcluidas(): ResponseEntity<MutableList<ViagemResumoDTO?>?> {
        val auth = SecurityContextHolder.getContext().getAuthentication()
        val email = auth?.name
        return ResponseEntity.ok()
            .body<MutableList<ViagemResumoDTO?>?>(compraService.listarViagensConcluidasDoUsuarios(email))
    }


    @GetMapping("/{id}")
    fun detalharViagem(@PathVariable id: Long): ResponseEntity<ViagemDetalhadaDTO?> {
        val auth = SecurityContextHolder.getContext().getAuthentication()
        val email = auth?.name
        return ResponseEntity.ok().body<ViagemDetalhadaDTO?>(compraService.buscarDetalhesViagem(id, email))
    }

    @PostMapping
    fun realizarCompra(@RequestBody dto: CompraRequestDTO): ResponseEntity<CompraResponseDTO?> {
        return ResponseEntity.ok().body<CompraResponseDTO?>(compraService.processarCompra(dto))
    }
}