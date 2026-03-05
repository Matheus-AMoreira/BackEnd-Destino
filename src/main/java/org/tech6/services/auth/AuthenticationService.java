package org.tech6.services.auth;

import org.tech6.dto.auth.login.LoginResponseDTO;
import org.tech6.dto.auth.login.LoginUsuarioDto;
import org.tech6.dto.auth.registro.RegistrationResponseDto;
import org.tech6.dto.auth.registro.RegistroDto;
import org.tech6.model.usuario.SessionToken;
import org.tech6.model.usuario.Usuario;
import org.tech6.repository.usuario.SessionRepository;
import org.tech6.repository.usuario.UsuarioRepository;
import org.tech6.util.model.usuario.perfil.UserRole;
import org.tech6.util.model.usuario.senha.SenhaValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Cookie;
import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;

@ApplicationScoped
public class AuthenticationService {

    private static final String COOKIE_NAME = "session_token";
    private static final long JWT_EXPIRATION_MS = 20 * 60 * 1000; // 20 minutos
    private static final int REFRESH_TOKEN_DAYS = 30; // 30 dias

    private final UsuarioRepository userRepository;
    private final JwtService jwtService;
    private final SessionRepository sessionRepository;

    public AuthenticationService(UsuarioRepository userRepository, JwtService jwtService,
            SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.sessionRepository = sessionRepository;
    }

    public static class LoginResult {
        public NewCookie cookie;
    }

    @Transactional
    public RegistrationResponseDto cadastrarUsuario(RegistroDto dados) {
        if (!SenhaValidator.isValid(dados.senha())) {
            return new RegistrationResponseDto(true, "A senha não atende aos requisitos de segurança.");
        }

        if (userRepository.findByCpf(dados.cpf()).isPresent()) {
            return new RegistrationResponseDto(true, "Erro: CPF já cadastrado.");
        }

        if (userRepository.findByEmail(dados.email()).isPresent()) {
            return new RegistrationResponseDto(true, "Erro: Email já está em uso.");
        }

        try {
            Usuario novoUsuario = new Usuario(
                    dados.nome(),
                    dados.sobreNome(),
                    dados.cpf().valor(),
                    dados.email(),
                    dados.telefone().getValorPuro(),
                    BCrypt.hashpw(dados.senha(), BCrypt.gensalt()),
                    false,
                    UserRole.USUARIO,
                    Collections.emptyList());

            userRepository.persist(novoUsuario);
            return new RegistrationResponseDto(false, "Usuário cadastrado com sucesso!");

        } catch (Exception e) {
            return new RegistrationResponseDto(true, "Erro interno ao cadastrar: " + e.getMessage());
        }
    }

    @Transactional
    public LoginResponseDTO realizarLogin(LoginUsuarioDto dadosLogin, LoginResult result) {
        try {
            Usuario usuario = userRepository.findByEmail(dadosLogin.email()).orElse(null);

            if (usuario == null || !BCrypt.checkpw(dadosLogin.senha(), usuario.senha)) {
                return new LoginResponseDTO(true, "Email ou senha incorretos.", null);
            }

            String jwtToken = jwtService.generateToken(usuario, JWT_EXPIRATION_MS);
            String refreshToken = gerarRefreshToken();

            long validadeEmSegundos = Instant.now().plus(REFRESH_TOKEN_DAYS, ChronoUnit.DAYS).getEpochSecond();

            salvarSessaoNoBanco(usuario, refreshToken, validadeEmSegundos);

            result.cookie = criarCookieSessao(refreshToken, validadeEmSegundos);

            return new LoginResponseDTO(
                    false,
                    "Login realizado com sucesso",
                    new LoginResponseDTO.UserInfo(
                            usuario.id,
                            usuario.nome + " " + usuario.sobreNome,
                            usuario.email,
                            usuario.role.name(),
                            jwtToken,
                            JWT_EXPIRATION_MS / 1000));

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
        sessionRepository.deleteUserSessionToken(usuario.id);
        SessionToken sessionToken = new SessionToken(token, validade, usuario);
        sessionRepository.persist(sessionToken);
    }

    // --- Métodos Auxiliares ---

    private NewCookie criarCookieSessao(String token, long maxAgeSeconds) {
        long duracaoCookie = maxAgeSeconds - Instant.now().getEpochSecond();

        return new NewCookie.Builder(COOKIE_NAME)
                .value(token)
                .path("/")
                .maxAge((int) duracaoCookie)
                .httpOnly(true)
                .secure(true)
                .sameSite(NewCookie.SameSite.NONE)
                .build();
    }

    @Transactional
    public LoginResponseDTO renovarToken(String tokenSessao, LoginResult result) {
        Optional<SessionToken> sessionTokenOpt = sessionRepository.findByToken(tokenSessao);

        if (sessionTokenOpt.isEmpty()) {
            result.cookie = limparCookie();
            return new LoginResponseDTO(true, "Sessão não encontrada.", null);
        }

        SessionToken sessao = sessionTokenOpt.get();
        Usuario usuario = sessao.usuario;

        if (Instant.now().getEpochSecond() > sessao.validade) {
            result.cookie = limparCookie();
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
                        usuario.id,
                        usuario.nome + " " + usuario.sobreNome,
                        usuario.email,
                        usuario.role.name(),
                        novoJwt,
                        JWT_EXPIRATION_MS / 1000));
    }

    private NewCookie limparCookie() {
        return new NewCookie.Builder(COOKIE_NAME)
                .value("")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(true)
                .sameSite(NewCookie.SameSite.NONE)
                .build();
    }

    @Transactional
    public void realizarLogout(String tokenSessao, LoginResult result) {
        if (tokenSessao != null) {
            Optional<SessionToken> sessao = sessionRepository.findByToken(tokenSessao);
            sessao.ifPresent(
                    sessionToken -> sessionRepository.deleteUserSessionToken(sessionToken.usuario.id));
        }
        result.cookie = limparCookie();
    }
}
