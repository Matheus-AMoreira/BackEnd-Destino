package com.fatec.destino.services.auth

import com.fatec.destino.dto.auth.login.LoginResponseDTO
import com.fatec.destino.dto.auth.login.LoginUsuarioDto
import com.fatec.destino.dto.auth.registro.UsuarioRegistrationResponseDto
import com.fatec.destino.dto.auth.registro.UsuarioRegistroDTO
import com.fatec.destino.model.usuario.SessionToken
import com.fatec.destino.model.usuario.Usuario
import com.fatec.destino.repository.auth.SessionRepository
import com.fatec.destino.repository.usuario.UsuarioRepository
import com.fatec.destino.util.model.usuario.senha.SenhaValidator
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.security.SecureRandom
import kotlin.io.encoding.Base64
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days


@Service
class AuthenticationService(
    private val userRepository: UsuarioRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val sessionRepository: SessionRepository
) {
    companion object {
        private const val COOKIE_NAME = "session_token"
        private const val JWT_EXPIRATION_MS = 20L * 60 * 1000 // 20 minutos
        private const val REFRESH_TOKEN_DAYS = 30L
    }

    fun cadastrarUsuario(dados: UsuarioRegistroDTO): UsuarioRegistrationResponseDto {
        if (!SenhaValidator.isValid(dados.senha)) {
            return UsuarioRegistrationResponseDto(true, "A senha não atende aos requisitos de segurança.")
        }

        if (userRepository.findByCpf(dados.cpf) == null) {
            return UsuarioRegistrationResponseDto(true, "Erro: CPF já cadastrado.")
        }

        if (userRepository.findByEmail(dados.email) == null) {
            return UsuarioRegistrationResponseDto(true, "Erro: Email já está em uso.")
        }

        try {
            val novoUsuario = Usuario(
                nome = dados.nome,
                sobreNome = dados.sobreNome,
                cpf = dados.cpf,
                email = dados.email,
                telefone = dados.telefone,
                senha = passwordEncoder.encode(dados.senha)!!
            )

            userRepository.save<Usuario>(novoUsuario)
            return UsuarioRegistrationResponseDto(false, "Usuário cadastrado com sucesso!")
        } catch (e: Exception) {
            return UsuarioRegistrationResponseDto(true, "Erro interno ao cadastrar: " + e.message)
        }
    }

    fun realizarLogin(dadosLogin: LoginUsuarioDto, response: HttpServletResponse): LoginResponseDTO {
        try {
            val authentication: Authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(dadosLogin.email, dadosLogin.senha)
            )

            val usuario = authentication.principal as Usuario

            val jwtToken = jwtService.generateToken(usuario, JWT_EXPIRATION_MS)
            val refreshToken = gerarRefreshToken()

            val validadeEmSegundos: Long = Clock.System.now().plus(REFRESH_TOKEN_DAYS.days).epochSeconds

            salvarSessaoNoBanco(usuario, refreshToken, validadeEmSegundos)

            criarCookieSessao(response, refreshToken, validadeEmSegundos)

            return LoginResponseDTO(
                false,
                "Login realizado com sucesso",
                LoginResponseDTO.UserInfo(
                    usuario.id,
                    usuario.nome + " " + usuario.sobreNome,
                    usuario.email,
                    usuario.perfil.toString(),
                    jwtToken,
                    JWT_EXPIRATION_MS / 1000
                )
            )
        } catch (_: BadCredentialsException) {
            return LoginResponseDTO(true, "Email ou senha incorretos.", null)
        } catch (e: Exception) {
            return LoginResponseDTO(true, "Erro ao realizar login: " + e.message, null)
        }
    }

    private fun gerarRefreshToken(): String {
        val random: SecureRandom = SecureRandom()
        val bytes = ByteArray(64)
        random.nextBytes(bytes)
        return Base64.UrlSafe.encode(bytes)
    }

    private fun salvarSessaoNoBanco(usuario: Usuario, token: String, validade: Long) {
        sessionRepository.deleteUserSessionToken(usuario.id)
        val sessionToken = SessionToken(token, validade, usuario)
        sessionRepository.save<SessionToken>(sessionToken)
    }

    // --- Métodos Auxiliares ---
    private fun criarCookieSessao(response: HttpServletResponse, token: String?, maxAgeSeconds: Long) {
        val duracaoCookie: Long = maxAgeSeconds - Clock.System.now().epochSeconds

        val cookie = ResponseCookie.from(COOKIE_NAME, token)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(duracaoCookie)
            .sameSite("None")
            .build()

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
    }

    fun renovarToken(tokenSessao: String, response: HttpServletResponse): LoginResponseDTO {
        val session: SessionToken? = sessionRepository.findByToken(tokenSessao)

        if (session == null) {
            limparCookie(response)
            return LoginResponseDTO(true, "Sessão não encontrada.", null)
        }

        val usuario = session.usuario

        if (Clock.System.now().epochSeconds > session.validade) {
            limparCookie(response)
            sessionRepository.delete(session)
            return LoginResponseDTO(true, "Sessão expirada. Faça login novamente.", null)
        }

        // Gera novo JWT de 20 min
        val novoJwt = jwtService.generateToken(usuario, JWT_EXPIRATION_MS)

        // Monta a resposta com a estrutura correta
        return LoginResponseDTO(
            false,
            "Token renovado com sucesso.",
            LoginResponseDTO.UserInfo(
                usuario.id,
                usuario.nome + " " + usuario.sobreNome,
                usuario.email,
                usuario.perfil.toString(),
                novoJwt,
                JWT_EXPIRATION_MS / 1000
            )
        )
    }

    private fun limparCookie(response: HttpServletResponse) {
        val cookie = ResponseCookie.from(COOKIE_NAME, "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(0)
            .sameSite("None")
            .build()

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
    }

    @Transactional
    fun realizarLogout(tokenSessao: String, response: HttpServletResponse) {

        val session  = sessionRepository.findByToken(tokenSessao)
        if (session  == null){
            sessionRepository.deleteUserSessionToken(session )
        }

        limparCookie(response)
    }
}
