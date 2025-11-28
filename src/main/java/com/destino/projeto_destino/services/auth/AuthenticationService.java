package com.destino.projeto_destino.services.auth;


import com.destino.projeto_destino.dto.auth.login.LoginResponseDto;
import com.destino.projeto_destino.dto.auth.login.LoginUsuarioDto;
import com.destino.projeto_destino.dto.auth.registro.RegistrationResponseDto;
import com.destino.projeto_destino.dto.auth.registro.RegistroDto;
import com.destino.projeto_destino.model.usuario.SessionToken;
import com.destino.projeto_destino.model.usuario.Usuario;
import com.destino.projeto_destino.repository.usuario.SessionRepository;
import com.destino.projeto_destino.repository.usuario.UsuarioRepository;
import com.destino.projeto_destino.util.model.usuario.Cpf.Cpf;
import com.destino.projeto_destino.util.model.usuario.perfil.UserRole;
import com.destino.projeto_destino.util.model.usuario.senha.SenhaValidator;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final UsuarioRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SessionRepository sessionRepository;
    private final SecureRandom secureRandom;

    public ResponseEntity<RegistrationResponseDto> registrar(RegistroDto registrationRequest
    ) {
        if (!SenhaValidator.isValid(registrationRequest.senha())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new RegistrationResponseDto(true, "A senha não atende aos requisitos de segurança...")
            );
        }

        try {

            if (userRepository.findByCpf(new Cpf(registrationRequest.cpf())).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        new RegistrationResponseDto(true, "Erro: usuário já existe!"));
            }

            if (userRepository.findByEmail(registrationRequest.email()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        new RegistrationResponseDto(true, "Erro: Email já está em uso"));
            }

            Usuario user = userRepository.save(new Usuario(
                    registrationRequest.nome(),
                    registrationRequest.sobreNome(),
                    registrationRequest.cpf(),
                    registrationRequest.email(),
                    registrationRequest.telefone(),
                    passwordEncoder.encode(registrationRequest.senha()),
                    UserRole.USUARIO,
                    false
            ));

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new RegistrationResponseDto(false, "Usuário " + user.getNome() + user.getSobreNome() + "cadastrado com sucesso!"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new RegistrationResponseDto(true, "Falha na validação dos dados: " + e.getMessage()));
        }
    }

    @Transactional
    public ResponseEntity<LoginResponseDto> autenticar(
            LoginUsuarioDto user,
            HttpServletResponse response
    ) {
        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.email(),
                            user.senha()
                    )
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new LoginResponseDto(true, "Credenciais inválidas: e-mail ou senha incorretos.", Optional.empty())
            );
        }

        Usuario authenticatedUser = (Usuario) authentication.getPrincipal();

        int jwtExpiration = 1200000;
        String jwtToken = jwtService.generateToken(authenticatedUser, jwtExpiration);

        byte[] tokenBytes = new byte[32];
        this.secureRandom.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);

        SessionToken sessionToken = new SessionToken(
                token,
                Instant.now().plusSeconds(30 * 24 * 60 * 60),
                authenticatedUser
        );

        sessionRepository.save(sessionToken);

        ResponseCookie cookie = ResponseCookie.from("token", sessionToken.toString())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(30L * 24 * 60 * 60 * 1000)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(new LoginResponseDto(false, "Login realizado com sucesso.",
                Optional.of(new LoginResponseDto.UserInfo(
                        authenticatedUser.getId().toString(),
                        authenticatedUser.getNome() + " " + authenticatedUser.getSobreNome(),
                        authenticatedUser.getEmail(),
                        authenticatedUser.getPerfil().toString(),
                        jwtToken
                ))));
    }

    public ResponseEntity<LoginResponseDto> logout(HttpServletResponse response, String id) {
        ResponseCookie cookie = ResponseCookie.from("token", null)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        sessionRepository.deleteUserSessionToken(id);

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok().body(new LoginResponseDto(false, "Sucesso ao sair!", Optional.empty()));
    }
}
