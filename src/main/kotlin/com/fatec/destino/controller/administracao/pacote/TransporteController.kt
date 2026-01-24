package com.fatec.destino.controller.administracao.pacote

import com.fatec.destino.dto.transporte.TransporteRegistroDTO
import com.fatec.destino.model.pacote.transporte.Transporte
import com.fatec.destino.services.transporte.TransporteService
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

    @GetMapping
    fun transporte(): ResponseEntity<List<Transporte>> {
        return transporteService.pegarTransportes()
    }

    @GetMapping("/{id}")
    fun getTransporteById(@PathVariable id: Long): ResponseEntity<Transporte> {
        val transporte = transporteService.buscarPorId(id)
        return if (transporte != null) {
            ResponseEntity.ok(transporte)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun registrarTransporte(@RequestBody dto: TransporteRegistroDTO): ResponseEntity<String> {
        return transporteService.criarTransportes(dto)
    }

    @PutMapping("/{id}")
    fun atualizarTransporte(@PathVariable id: Long, @RequestBody dto: TransporteRegistroDTO): ResponseEntity<String> {
        return transporteService.atualizarTransporte(id, dto)
    }

    @DeleteMapping("/{id}")
    fun deletarTransporte(@PathVariable id: Long): ResponseEntity<String>  {
        return transporteService.deletarTransporte(id)
    }
}