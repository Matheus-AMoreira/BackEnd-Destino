package com.destino.projeto_destino.controller;

import com.destino.projeto_destino.dto.pacote.hotel.HotelRegistroDTO;
import com.destino.projeto_destino.dto.pacote.transporte.TransporteRegistroDTO;
import com.destino.projeto_destino.model.pacote.hotel.Hotel;
import com.destino.projeto_destino.model.pacote.transporte.Transporte;
import com.destino.projeto_destino.services.PacoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacote")
@EnableMethodSecurity(prePostEnabled = true)
public class PacoteController {

    private final PacoteService pacoteService;

    public PacoteController(PacoteService pacoteService) {
        this.pacoteService = pacoteService;
    }

    @GetMapping()
    public int getPacotes(){
        return 1;
    }

    @GetMapping("/{name}")
    public int getPacoteName(){
        return 1;
    }

    @PostMapping()
    public int postPacote(){
        return 1;
    }

    @GetMapping("/transporte")
    public ResponseEntity<List<Transporte>> getTransporte(){
        return pacoteService.pegarTransportes();
    }

    @PostMapping("/transporte")
    public ResponseEntity<String> postTransporte(@RequestBody TransporteRegistroDTO hotel){
        return pacoteService.criarTransportes(hotel);
    }

    @GetMapping("/hotel")
    public ResponseEntity<List<Hotel>> getHotel(){
        return pacoteService.pegarHoteis();
    }

    @PostMapping("/hotel")
    @PreAuthorize("hasRole('ROLE_FUNCIONARIO')")
    public ResponseEntity<String> postHotel(@RequestBody HotelRegistroDTO hotel){
        return pacoteService.criarHotel(hotel);
    }

    @GetMapping("/estatistica/status/viagem")
    public int getStatusNumber(){
        return 1;
    }

    @GetMapping("/estatistica/transporte")
    public int getTransporteNumber(){
        return 1;
    }

    @GetMapping("/estatistica/viagens/mensais")
    public int getViagensMensais(){
        return 1;
    }

    @GetMapping("/estatistica/viagens/vendidos")
    public int getViagensMaisVendidas(){
        return 1;
    }
}
