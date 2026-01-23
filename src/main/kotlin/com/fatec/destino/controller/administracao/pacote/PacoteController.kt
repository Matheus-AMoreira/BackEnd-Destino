package com.fatec.destino.controller.administracao.pacote

import com.fatec.destino.dto.pacote.PacoteRegistroDTO
import com.fatec.destino.model.pacote.Pacote
import com.fatec.destino.services.pacote.PacoteService
import org.springframework.http.ResponseEntity
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/pacote")
@EnableMethodSecurity(prePostEnabled = true)
class PacoteController(
    private val pacoteService: PacoteService
) {
    @GetMapping("/agrupado-admin")
    fun pacotesAdmin() : ResponseEntity<MutableMap<String?, MutableList<Pacote?>?>?>
        return pacoteService.pegarPacotesAgrupadosPorLocal()

    @PostMapping
    fun registrarPacote(@RequestBody pacoteRegistroDTO: PacoteRegistroDTO?): ResponseEntity<String?> {
        return pacoteService.salvarOuAtualizar(pacoteRegistroDTO, null)
    }

    @PutMapping("/{id}")
    fun atualizarPacote(
        @PathVariable id: Int,
        @RequestBody pacoteRegistroDTO: PacoteRegistroDTO?
    ): ResponseEntity<String?> {
        return pacoteService.salvarOuAtualizar(pacoteRegistroDTO, id)
    }
}