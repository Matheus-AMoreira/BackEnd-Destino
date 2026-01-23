package com.fatec.destino.controller.administracao.pacote

import com.fatec.destino.dto.pacote.hotel.HotelRegistroDTO
import com.fatec.destino.model.pacote.hotel.Hotel
import com.fatec.destino.model.pacote.hotel.cidade.Cidade
import com.fatec.destino.model.pacote.hotel.cidade.estado.Estado
import com.fatec.destino.model.pacote.hotel.cidade.estado.regiao.Regiao
import com.fatec.destino.services.pacote.hotel.HotelService
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
@RequestMapping("/api/hotel")
@EnableMethodSecurity(prePostEnabled = true)
class HotelController(private val hotelService: HotelService) {
    @GetMapping
    fun procurarHoteis(): ResponseEntity<MutableList<Hotel?>?> {
        return ResponseEntity.ok().body<MutableList<Hotel?>?>(hotelService.pegarHoteis())
    }

    // Endpoint para buscar hotel específico para edição
    @GetMapping("/{id}")
    fun getHotelById(@PathVariable id: Long): ResponseEntity<Hotel?> {
        return ResponseEntity.ok().body<Hotel?>(hotelService.pegarHotelById(id))
    }

    @get:GetMapping("/regioes")
    val regioes: ResponseEntity<MutableList<Regiao?>?>
        get() = hotelService.listarRegioes()

    @GetMapping("/estados/{regiaoId}")
    fun getEstadosByRegiao(@PathVariable regiaoId: Long?): ResponseEntity<MutableList<Estado?>?> {
        return hotelService.listarEstadosPorRegiao(regiaoId)
    }

    @GetMapping("/cidades/{estadoId}")
    fun getCidadesByEstado(@PathVariable estadoId: Long?): ResponseEntity<MutableList<Cidade?>?> {
        return hotelService.listarCidadesPorEstado(estadoId)
    }

    @GetMapping("/cidades")
    fun listarCidades(): ResponseEntity<MutableList<Cidade?>?> {
        return hotelService.pegarTodasCidades()
    }

    @GetMapping("/regiao/{regiao}")
    fun pegarCidadadesPorRegiao(@PathVariable regiao: String?): ResponseEntity<MutableList<Cidade?>?> {
        return hotelService.pegarCidadePorRegiao(regiao)
    }

    @GetMapping("/cidade/regiao/{regiao}")
    fun pegarHotelPorRegiao(@PathVariable regiao: String?): ResponseEntity<MutableList<Hotel?>?> {
        return hotelService.pegarHotelPorRegiao(regiao)
    }

    @PostMapping
    fun registrarHotel(@RequestBody hotel: HotelRegistroDTO): ResponseEntity<String?> {
        return hotelService.criarHotel(hotel)
    }

    @PutMapping("/{id}")
    fun atualizarHotel(@PathVariable id: Long, @RequestBody hotel: HotelRegistroDTO): ResponseEntity<String?> {
        return hotelService.atualizarHotel(id, hotel)
    }

    @DeleteMapping("/{id}")
    fun deletarHotel(@PathVariable id: Long): ResponseEntity<String?> {
        return hotelService.deletarHotel(id)
    }
}