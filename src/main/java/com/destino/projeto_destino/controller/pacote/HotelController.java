package com.destino.projeto_destino.controller.pacote;

import com.destino.projeto_destino.dto.pacote.hotel.HotelRegistroDTO;
import com.destino.projeto_destino.model.pacote.hotel.Hotel;
import com.destino.projeto_destino.model.pacote.hotel.cidade.Cidade;
import com.destino.projeto_destino.services.pacote.HotelService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/")
    public ResponseEntity<List<Hotel>> procurarHoteis() {
        return hotelService.pegarHoteis();
    }

    @GetMapping("/regiao/{regiao}")
    public ResponseEntity<List<Cidade>> pegarCidadadesPorRegiao(@PathVariable String regiao) {
        return hotelService.pegarCidadePorRegiao(regiao);
    }

    @GetMapping("/cidade/regiao/{regiao}")
    public ResponseEntity<List<Hotel>> pegarHotelPorRegiao(@PathVariable String regiao) {
        return hotelService.pegarHotelPorRegiao(regiao);
    }

    @PostMapping("/")
    public ResponseEntity<String> registrarHotel(@RequestBody HotelRegistroDTO hotel) {
        System.out.println(hotel.toString());
        return hotelService.criarHotel(hotel);
    }
}
