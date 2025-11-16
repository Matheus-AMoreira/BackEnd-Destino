package com.destino.projeto_destino.services;

import com.destino.projeto_destino.dto.UsuarioDTO;
import com.destino.projeto_destino.dto.auth.login.LoginResponseDto;
import com.destino.projeto_destino.dto.auth.login.LoginUsuarioDto;
import com.destino.projeto_destino.dto.auth.registro.RegistrationResponseDto;
import com.destino.projeto_destino.dto.auth.registro.RegistroDto;
import com.destino.projeto_destino.dto.auth.validar.ValidarResponseDTO;
import com.destino.projeto_destino.model.usuario.Usuario;
import com.destino.projeto_destino.repository.UserRepository;
import com.destino.projeto_destino.util.usuario.Cpf.Cpf;
import com.destino.projeto_destino.util.usuario.Email.Email;
import com.destino.projeto_destino.util.usuario.perfil.UserRole;
import com.destino.projeto_destino.util.usuario.senha.SenhaValidator;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
            user.setSobreNome(registrationRequest.sobreNome());
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

        ResponseCookie cookie = ResponseCookie.from("jwt", jwtToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(jwtService.getExpirationTime() / 1000)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(new LoginResponseDto(false, "Login realizado com sucesso.", Optional.of(new LoginResponseDto.UserInfo(authenticatedUser.getId().toString(), authenticatedUser.getNome()))));
    }

    public ResponseEntity<List<UsuarioDTO>> inValidUsers() {
        List<Usuario> usuarios = userRepository.findByValidoFalse();

        List<UsuarioDTO> usuariosDTO = usuarios.stream().map(usuario -> new UsuarioDTO(
                usuario.getId(),
                usuario.getNome() + " " + usuario.getSobreNome(),
                usuario.getCpf(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getPerfil(),
                usuario.getValido(),
                usuario.getCadastro(),
                usuario.getCadastro())).collect(Collectors.toList());

        return ResponseEntity.ok().body(usuariosDTO);
    }

    public void logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("jwt", null)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @Transactional
    public ResponseEntity<ValidarResponseDTO> validar(UUID id) {
        int linhasAfetadas = userRepository.validarUsuario(id);

        if (linhasAfetadas == 0) {
            return ResponseEntity.ok().body(new ValidarResponseDTO(true, "Não existe usuario com id: " + id));
        }

        return ResponseEntity.ok().body(new ValidarResponseDTO(false, "Usuário atualizado com sucesso!"));
    }
}
