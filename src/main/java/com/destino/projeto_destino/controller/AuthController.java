package com.destino.projeto_destino.controller;

import com.destino.projeto_destino.dto.UsuarioDTO;
import com.destino.projeto_destino.dto.auth.login.LoginResponseDto;
import com.destino.projeto_destino.dto.auth.login.LoginUsuarioDto;
import com.destino.projeto_destino.dto.auth.registro.RegistrationResponseDto;
import com.destino.projeto_destino.dto.auth.registro.RegistroDto;
import com.destino.projeto_destino.dto.auth.validar.ValidarResponseDTO;
import com.destino.projeto_destino.services.AuthenticationService;
import com.destino.projeto_destino.services.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@EnableMethodSecurity(prePostEnabled = true)
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(JwtService jwtService, AuthenticationService authenticationService, HttpServletResponse httpServletResponse) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/singup")
    public ResponseEntity<RegistrationResponseDto> registerUser(@Valid @RequestBody RegistroDto usuario) {
        return authenticationService.registrar(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> authenticate(
            @Valid @RequestBody LoginUsuarioDto loginUserDto,
            HttpServletResponse response
    ) {
        return authenticationService.autenticar(loginUserDto, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> userLogout(
            HttpServletResponse response
    ) {
        authenticationService.logout(response);
        return ResponseEntity.ok().body("Logout realizado!");
    }

    @GetMapping("/usuarios/invalidos")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<List<UsuarioDTO>> inValiduser() {
        return authenticationService.inValidUsers();
    }

    @PatchMapping("/usuarios/validar/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<ValidarResponseDTO> validUser(@PathVariable UUID id) {
        return authenticationService.validar(id);
    }
}