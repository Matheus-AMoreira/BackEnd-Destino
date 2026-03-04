package com.fatec.destino.controller.administracao.pacote;

import com.fatec.destino.dto.pacote.OfertaRegistroDTO;
import com.fatec.destino.services.pacote.OfertaService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pacote/oferta")
@EnableMethodSecurity(prePostEnabled = true)
@AllArgsConstructor
public class OfertaController {

    private final OfertaService ofertaService;

    @PostMapping("/registrar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'FUNCIONARIO')")
    public ResponseEntity<String> registrarOferta(@RequestBody OfertaRegistroDTO dto) {
        return ofertaService.registrarOferta(dto);
    }
}
