package com.destino.projeto_destino.controller.usuario;

import com.destino.projeto_destino.dto.UsuarioDTO;
import com.destino.projeto_destino.dto.auth.validar.ValidarResponseDTO;
import com.destino.projeto_destino.model.usuario.Usuario;
import com.destino.projeto_destino.services.usuario.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuario")
@EnableMethodSecurity(prePostEnabled = true)
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/invalidos")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<List<UsuarioDTO>> inValiduser() {
        return usuarioService.inValidUsers();
    }

    @PatchMapping("/validar/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<ValidarResponseDTO> validUser(@PathVariable UUID id) {
        return usuarioService.validar(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_FUNCIONARIO')")
    public ResponseEntity<Iterable<Usuario>> buscarUsuarios() {
        return ResponseEntity.ok().body(usuarioService.buscarUsuarios());
    }
}
