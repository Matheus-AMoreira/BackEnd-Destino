package com.fatec.destino.controller.administracao.pacote

import com.fatec.destino.dto.pacote.transporte.TransporteRegistroDTO
import com.fatec.destino.model.pacote.transporte.Transporte
import com.fatec.destino.services.pacote.Transporte.TransporteService
import org.springframework.http.ResponseEntity
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/transporte")
@EnableMethodSecurity(prePostEnabled = true)
class TransporteController(
    private val transporteService: TransporteService
) {

    @get:GetMapping
    val transporte: ResponseEntity<MutableList<Transporte?>?>
        get() = transporteService.pegarTransportes()

    @GetMapping("/{id}")
    fun getTransporteById(@PathVariable id: Int): ResponseEntity<Transporte?> {
        return transporteService.pegarTransportes().getBody().stream()
            .filter({ t -> t.getId() === id })
            .findFirst()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build<T?>())
    }

    @PostMapping
    fun registrarTransporte(@RequestBody hotel: TransporteRegistroDTO?): ResponseEntity<String?> {
        return transporteService.criarTransportes(hotel)
    }

    @PutMapping("/{id}")
    fun atualizarTransporte(@PathVariable id: Int, @RequestBody dto: TransporteRegistroDTO?): ResponseEntity<String?> {
        return transporteService.atualizarTransporte(id, dto)
    }

    @DeleteMapping("/{id}")
    fun deletarTransporte(@PathVariable id: Int): ResponseEntity<String?> {
        return transporteService.deletarTransporte(id)
    }
}