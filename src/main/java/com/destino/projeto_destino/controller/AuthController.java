package com.destino.projeto_destino.controller;

import com.destino.projeto_destino.dto.LoginResponseDto;
import com.destino.projeto_destino.dto.LoginUsuarioDto;
import com.destino.projeto_destino.dto.RegistrationResponseDto;
import com.destino.projeto_destino.dto.RegistroDto;
import com.destino.projeto_destino.model.Usuario;
import com.destino.projeto_destino.services.AuthenticationService;
import com.destino.projeto_destino.services.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

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
        return authenticationService.signup(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> authenticate(
            @Valid @RequestBody LoginUsuarioDto loginUserDto,
            HttpServletResponse response
    ) {
        return authenticationService.authenticate(loginUserDto, response);
    }

    @GetMapping("/usuarios/invalidos")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<List<Usuario>> inValiduser(){
        return authenticationService.inValidUsers();
    }

    @PatchMapping("/usuarios/validar/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<String> validUser(@PathVariable UUID id){
        return authenticationService.validar(id);
    }
}