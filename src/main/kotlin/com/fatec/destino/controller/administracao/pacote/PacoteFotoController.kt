package com.fatec.destino.controller.administracao.pacote

import com.fatec.destino.dto.pacoteFoto.PacoteFotoRegistroDTO
import com.fatec.destino.model.pacote.pacoteFoto.PacoteFoto
import com.fatec.destino.services.pacoteFoto.PacoteFotoService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/pacote-foto")
@PreAuthorize("hasRole('ROLE_FUNCIONARIO')")
class PacoteFotoController(private val service: PacoteFotoService) {
    @GetMapping
    fun listar(): ResponseEntity<List<PacoteFoto>> {
        return service.listarPacotesFoto()
    }

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Long): ResponseEntity<PacoteFoto> {
        return service.buscarPorId(id)
    }

    @PostMapping
    fun criar(@RequestBody dto: PacoteFotoRegistroDTO): ResponseEntity<String> {
        return service.criarPacoteFoto(dto)
    }

    @PutMapping("/{id}")
    fun atualizar(@PathVariable id: Long, @RequestBody dto: PacoteFotoRegistroDTO): ResponseEntity<String> {
        return service.atualizarPacoteFoto(id, dto)
    }
}