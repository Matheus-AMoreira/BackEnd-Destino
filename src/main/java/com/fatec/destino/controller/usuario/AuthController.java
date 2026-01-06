package com.fatec.destino.controller.usuario;

import com.fatec.destino.dto.auth.login.LoginResponseDTO;
import com.fatec.destino.dto.auth.login.LoginUsuarioDto;
import com.fatec.destino.dto.auth.registro.RegistrationResponseDto;
import com.fatec.destino.dto.auth.registro.RegistroDto;
import com.fatec.destino.services.auth.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private static final String COOKIE_NAME = "session_token";

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<RegistrationResponseDto> cadastrar(@Valid @RequestBody RegistroDto usuario) {
        RegistrationResponseDto resposta = authenticationService.cadastrarUsuario(usuario);

        if (resposta.erro()) {
            return ResponseEntity.ok().body(resposta);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PostMapping("/entrar")
    public ResponseEntity<LoginResponseDTO> entrar(
            @Valid @RequestBody LoginUsuarioDto loginDto,
            HttpServletResponse response
    ) {
        LoginResponseDTO resposta = authenticationService.realizarLogin(loginDto, response);

        if (resposta.erro()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resposta);
        }
        return ResponseEntity.ok(resposta);
    }

    @PostMapping("/renovar-token")
    public ResponseEntity<LoginResponseDTO> renovarToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String tokenSessao = extrairTokenDoCookie(request);
        if (tokenSessao == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponseDTO(true, "Cookie de sessão não encontrado.", null));
        }

        LoginResponseDTO resposta = authenticationService.renovarToken(tokenSessao, response);

        if (resposta.erro()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resposta);
        }
        return ResponseEntity.ok(resposta);
    }

    private String extrairTokenDoCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    @PostMapping("/sair")
    public ResponseEntity<LoginResponseDTO> sair(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String tokenSessao = extrairTokenDoCookie(request);
        authenticationService.realizarLogout(tokenSessao, response);
        return ResponseEntity.ok(new LoginResponseDTO(false, "Logout realizado com sucesso.", null));
    }
}