package com.destino.projeto_destino.services;

import com.destino.projeto_destino.dto.LoginResponseDto;
import com.destino.projeto_destino.dto.LoginUsuarioDto;
import com.destino.projeto_destino.dto.RegistrationResponseDto;
import com.destino.projeto_destino.dto.RegistroDto;
import com.destino.projeto_destino.model.usuarioUtils.Cpf.Cpf;
import com.destino.projeto_destino.model.usuarioUtils.Email.Email;
import com.destino.projeto_destino.model.usuarioUtils.UserRole;
import com.destino.projeto_destino.model.Usuario;
import com.destino.projeto_destino.repository.UserRepository;
import com.destino.projeto_destino.validar.SenhaValidator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder, JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public ResponseEntity<RegistrationResponseDto> signup(RegistroDto registrationRequest
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

            if (userRepository.findByEmail(new Email(registrationRequest.email())).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        new RegistrationResponseDto(true, "Erro: Email já está em uso"));
            }

            Usuario user = new Usuario();
            user.setNome(registrationRequest.nome());
            user.setCpf(registrationRequest.cpf());
            user.setEmail(registrationRequest.email());
            user.setTelefone(registrationRequest.telefone());
            user.setSenha(passwordEncoder.encode(registrationRequest.senha()));
            user.setPerfil(UserRole.USUARIO);
            user.setValido(false);

            userRepository.save(user);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new RegistrationResponseDto(false, "Usuário cadastrado com sucesso!"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new RegistrationResponseDto(true, "Falha na validação dos dados: " + e.getMessage()));
        }
    }

    public ResponseEntity<LoginResponseDto> authenticate(
            LoginUsuarioDto user,
            HttpServletResponse response
    ) {
        try {
            authenticationManager.authenticate(
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

        Usuario authenticatedUser = userRepository.findByEmail(new Email(user.email()))
                .orElseThrow();

        String jwtToken = jwtService.generateToken(authenticatedUser);

        Cookie cookie = new Cookie("jwt", jwtToken);

        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtService.getExpirationTime() / 1000));

        response.addCookie(cookie);

        return ResponseEntity.ok(new LoginResponseDto(false,"Login realizado com sucesso.", Optional.of(new LoginResponseDto.UserInfo(authenticatedUser.getId().toString(), authenticatedUser.nome()))));
    }
}
