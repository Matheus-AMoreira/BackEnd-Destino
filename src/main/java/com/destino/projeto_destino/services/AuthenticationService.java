package com.destino.projeto_destino.services;

import com.destino.projeto_destino.dto.LoginResponseDto;
import com.destino.projeto_destino.dto.LoginUsuarioDto;
import com.destino.projeto_destino.dto.RegistrationResponseDto;
import com.destino.projeto_destino.dto.RegistroDto;
import com.destino.projeto_destino.model.UsuarioUtils.Cpf.Cpf;
import com.destino.projeto_destino.model.UsuarioUtils.Email.Email;
import com.destino.projeto_destino.model.UsuarioUtils.UserRole;
import com.destino.projeto_destino.model.Usuario;
import com.destino.projeto_destino.repository.UserRepository;
import com.destino.projeto_destino.validar.SenhaValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public LoginResponseDto authenticate(LoginUsuarioDto user) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.email(),
                        user.senha()
                )
        );

        Usuario authenticatedUser = userRepository.findByEmail(new Email(user.email()))
                .orElseThrow();

        String jwtToken = jwtService.generateToken(authenticatedUser);

        return new LoginResponseDto("Bearer " + jwtToken, jwtService.getExpirationTime());
    }
}
