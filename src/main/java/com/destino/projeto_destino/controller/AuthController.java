package com.destino.projeto_destino.controller;

import com.destino.projeto_destino.dto.LoginResponseDto;
import com.destino.projeto_destino.dto.LoginUsuarioDto;
import com.destino.projeto_destino.dto.RegistrationResponseDto;
import com.destino.projeto_destino.dto.RegistroDto;
import com.destino.projeto_destino.model.Usuario;
import com.destino.projeto_destino.services.AuthenticationService;
import com.destino.projeto_destino.services.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@EnableMethodSecurity(prePostEnabled = true)
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(JwtService jwtService, AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDto> registerUser(@Valid @RequestBody RegistroDto usuario) {
        return authenticationService.signup(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> authenticate(@Valid @RequestBody LoginUsuarioDto loginUserDto) {
        LoginResponseDto loginResponse = authenticationService.authenticate(loginUserDto);
        return ResponseEntity.ok(loginResponse);
    }
}