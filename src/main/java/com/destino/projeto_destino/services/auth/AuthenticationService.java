package com.destino.projeto_destino.services.auth;


import com.destino.projeto_destino.dto.auth.login.LoginResponseDTO;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final String COOKIE_NAME = "session_token";
    private static final long JWT_EXPIRATION_MS = 20 * 60 * 1000; // 20 minutos
    private static final int REFRESH_TOKEN_DAYS = 30; // 30 dias

    private final UsuarioRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SessionRepository sessionRepository;

    public RegistrationResponseDto cadastrarUsuario(RegistroDto dados) {
        if (!SenhaValidator.isValid(dados.senha())) {
            return new RegistrationResponseDto(true, "A senha não atende aos requisitos de segurança.");
        }

        if (userRepository.findByCpf(new Cpf(dados.cpf())).isPresent()) {
            return new RegistrationResponseDto(true, "Erro: CPF já cadastrado.");
        }

        if (userRepository.findByEmail(dados.email()).isPresent()) {
            return new RegistrationResponseDto(true, "Erro: Email já está em uso.");
        }

        try {
            Usuario novoUsuario = new Usuario(
                    dados.nome(),
                    dados.sobreNome(),
                    dados.cpf(),
                    dados.email(),
                    dados.telefone(),
                    passwordEncoder.encode(dados.senha()),
                    UserRole.USUARIO,
                    false
            );

            userRepository.save(novoUsuario);
            return new RegistrationResponseDto(false, "Usuário cadastrado com sucesso!");

        } catch (Exception e) {
            return new RegistrationResponseDto(true, "Erro interno ao cadastrar: " + e.getMessage());
        }
    }

    public LoginResponseDTO realizarLogin(LoginUsuarioDto dadosLogin, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dadosLogin.email(), dadosLogin.senha())
            );

            Usuario usuario = (Usuario) authentication.getPrincipal();

            String jwtToken = jwtService.generateToken(usuario, JWT_EXPIRATION_MS);
            String refreshToken = gerarRefreshToken();

            long validadeEmSegundos = Instant.now().plus(REFRESH_TOKEN_DAYS, ChronoUnit.DAYS).getEpochSecond();

            salvarSessaoNoBanco(usuario, refreshToken, validadeEmSegundos);

            criarCookieSessao(response, refreshToken, validadeEmSegundos);

            return new LoginResponseDTO(
                    false,
                    "Login realizado com sucesso",
                    new LoginResponseDTO.UserInfo(
                            usuario.getId(),
                            usuario.getNome() + " " + usuario.getSobreNome(),
                            usuario.getEmail(),
                            usuario.getPerfil().toString(),
                            jwtToken,
                            JWT_EXPIRATION_MS / 1000
                    )
            );

        } catch (BadCredentialsException e) {
            return new LoginResponseDTO(true, "Email ou senha incorretos.", null);
        } catch (Exception e) {
            return new LoginResponseDTO(true, "Erro ao realizar login: " + e.getMessage(), null);
        }
    }

    private String gerarRefreshToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[64];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private void salvarSessaoNoBanco(Usuario usuario, String token, long validade) {
        sessionRepository.deleteUserSessionToken(usuario.getId());
        SessionToken sessionToken = new SessionToken(token, validade, usuario);
        sessionRepository.save(sessionToken);
    }

    // --- Métodos Auxiliares ---

    private void criarCookieSessao(HttpServletResponse response, String token, long maxAgeSeconds) {
        long duracaoCookie = maxAgeSeconds - Instant.now().getEpochSecond();

        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(duracaoCookie)
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public LoginResponseDTO renovarToken(String tokenSessao, HttpServletResponse response) {
        Optional<SessionToken> sessionTokenOpt = sessionRepository.findByToken(tokenSessao);

        if (sessionTokenOpt.isEmpty()) {
            limparCookie(response);
            return new LoginResponseDTO(true, "Sessão não encontrada.", null);
        }

        SessionToken sessao = sessionTokenOpt.get();
        Usuario usuario = sessao.getUsuario();

        if (Instant.now().getEpochSecond() > sessao.getValidade()) {
            limparCookie(response);
            sessionRepository.delete(sessao);
            return new LoginResponseDTO(true, "Sessão expirada. Faça login novamente.", null);
        }

        // Gera novo JWT de 20 min
        String novoJwt = jwtService.generateToken(usuario, JWT_EXPIRATION_MS);

        // Monta a resposta com a estrutura correta
        return new LoginResponseDTO(
                false,
                "Token renovado com sucesso.",
                new LoginResponseDTO.UserInfo(
                        usuario.getId(),
                        usuario.getNome() + " " + usuario.getSobreNome(),
                        usuario.getEmail(),
                        usuario.getPerfil().toString(),
                        novoJwt,
                        JWT_EXPIRATION_MS / 1000
                )
        );
    }

    private void limparCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @Transactional
    public void realizarLogout(String tokenSessao, HttpServletResponse response) {
        if (tokenSessao != null) {
            Optional<SessionToken> sessao = sessionRepository.findByToken(tokenSessao);
            sessao.ifPresent(sessionToken -> sessionRepository.deleteUserSessionToken(sessionToken.getUsuario().getId()));
        }
        limparCookie(response);
    }
}
