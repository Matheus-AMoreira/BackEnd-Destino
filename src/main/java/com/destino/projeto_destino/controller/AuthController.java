package com.destino.projeto_destino.controller;

import com.destino.projeto_destino.dto.auth.login.LoginResponseDto;
import com.destino.projeto_destino.dto.auth.login.LoginUsuarioDto;
import com.destino.projeto_destino.dto.auth.registro.RegistrationResponseDto;
import com.destino.projeto_destino.dto.auth.registro.RegistroDto;
import com.destino.projeto_destino.services.auth.AuthenticationService;
import com.destino.projeto_destino.services.auth.JwtService;
import jakarta.servlet.http.HttpServletResponse;
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

    public AuthController(JwtService jwtService, AuthenticationService authenticationService, HttpServletResponse httpServletResponse) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/singup")
    public ResponseEntity<RegistrationResponseDto> registrarUsusario(@Valid @RequestBody RegistroDto usuario) {
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

}