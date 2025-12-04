package com.destino.projeto_destino.controller.administracao.pacote;

import com.destino.projeto_destino.dto.pacote.hotel.HotelRegistroDTO;
import com.destino.projeto_destino.model.pacote.hotel.Hotel;
import com.destino.projeto_destino.model.pacote.hotel.cidade.Cidade;
import com.destino.projeto_destino.model.pacote.hotel.cidade.estado.Estado;
import com.destino.projeto_destino.model.pacote.hotel.cidade.estado.regiao.Regiao;
import com.destino.projeto_destino.services.pacote.HotelService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/hotel")
@EnableMethodSecurity(prePostEnabled = true)
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    public ResponseEntity<List<Hotel>> procurarHoteis() {
        return ResponseEntity.ok().body(hotelService.pegarHoteis());
    }

    // Endpoint para buscar hotel específico para edição
    @GetMapping("/{id}")
    public ResponseEntity<Hotel> getHotelById(@PathVariable long id) {
        return ResponseEntity.ok().body(hotelService.pegarHotelById(id));
    }

    @GetMapping("/regioes")
    public ResponseEntity<List<Regiao>> getRegioes() {
        return hotelService.listarRegioes();
    }

    @GetMapping("/estados/{regiaoId}")
    public ResponseEntity<List<Estado>> getEstadosByRegiao(@PathVariable Long regiaoId) {
        return hotelService.listarEstadosPorRegiao(regiaoId);
    }

    @GetMapping("/cidades/{estadoId}")
    public ResponseEntity<List<Cidade>> getCidadesByEstado(@PathVariable Long estadoId) {
        return hotelService.listarCidadesPorEstado(estadoId);
    }

    @GetMapping("/cidades")
    public ResponseEntity<List<Cidade>> listarCidades() {
        return hotelService.pegarTodasCidades();
    }

    @GetMapping("/regiao/{regiao}")
    public ResponseEntity<List<Cidade>> pegarCidadadesPorRegiao(@PathVariable String regiao) {
        return hotelService.pegarCidadePorRegiao(regiao);
    }

    @GetMapping("/cidade/regiao/{regiao}")
    public ResponseEntity<List<Hotel>> pegarHotelPorRegiao(@PathVariable String regiao) {
        return hotelService.pegarHotelPorRegiao(regiao);
    }

    @PostMapping
    public ResponseEntity<String> registrarHotel(@RequestBody HotelRegistroDTO hotel) {
        return hotelService.criarHotel(hotel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizarHotel(@PathVariable long id, @RequestBody HotelRegistroDTO hotel) {
        return hotelService.atualizarHotel(id, hotel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarHotel(@PathVariable long id) {
        return hotelService.deletarHotel(id);
    }
}
