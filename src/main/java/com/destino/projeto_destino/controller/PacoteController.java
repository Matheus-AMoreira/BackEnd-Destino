package com.destino.projeto_destino.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pacote")
@EnableMethodSecurity(prePostEnabled = true)
public class PacoteController {

    @GetMapping()
    public int getPacotes(){
        return 1;
    }

    @GetMapping("/{name}")
    public int getPacoteName(){
        return 1;
    }

    @GetMapping("/local")
    public int getLocais(){
        return 1;
    }

    @GetMapping("/transporte")
    public int getTransporte(){
        return 1;
    }

    @GetMapping("/hotel")
    public int getHotel(){
        return 1;
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
