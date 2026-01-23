package com.fatec.destino.controller.usuario

import com.fatec.destino.dto.auth.login.LoginResponseDTO
import com.fatec.destino.dto.auth.login.LoginUsuarioDto
import com.fatec.destino.dto.auth.registro.RegistrationResponseDto
import com.fatec.destino.dto.auth.registro.RegistroDto
import com.fatec.destino.dto.auth.registro.UsuarioRegistrationResponseDto
import com.fatec.destino.services.auth.AuthenticationService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*


@RestController
@RequestMapping("/api/auth")
class AuthController(private val authenticationService: AuthenticationService) {
    @PostMapping("/cadastrar")
    fun cadastrar(@Valid @RequestBody usuario: @Valid RegistroDto): ResponseEntity<RegistrationResponseDto?> {
        val resposta: UsuarioRegistrationResponseDto = authenticationService.cadastrarUsuario(usuario)

        if (resposta.erro) {
            return ResponseEntity.ok().body<RegistrationResponseDto?>(resposta)
        }
        return ResponseEntity.status(HttpStatus.CREATED).body<RegistrationResponseDto?>(resposta)
    }

    @PostMapping("/entrar")
    fun entrar(
        @Valid @RequestBody loginDto: @Valid LoginUsuarioDto,
        response: HttpServletResponse
    ): ResponseEntity<LoginResponseDTO?> {
        val resposta = authenticationService.realizarLogin(loginDto, response)

        if (resposta.erro) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body<LoginResponseDTO?>(resposta)
        }
        return ResponseEntity.ok<LoginResponseDTO?>(resposta)
    }

    @PostMapping("/renovar-token")
    fun renovarToken(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<LoginResponseDTO?> {
        val tokenSessao = extrairTokenDoCookie(request) ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body<LoginResponseDTO?>(LoginResponseDTO(true, "Cookie de sessão não encontrado.", null))

        val resposta = authenticationService.renovarToken(tokenSessao, response)

        if (resposta.erro) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body<LoginResponseDTO?>(resposta)
        }
        return ResponseEntity.ok<LoginResponseDTO?>(resposta)
    }

    private fun extrairTokenDoCookie(request: HttpServletRequest): String? {
        if (request.cookies == null) return null

        return Arrays.stream(request.getCookies())
            .filter({ cookie -> COOKIE_NAME == cookie.name })
            .map(Cookie::getValue)
            .findFirst()
            .orElse(null)
    }

    @PostMapping("/sair")
    fun sair(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<LoginResponseDTO?> {
        val tokenSessao = extrairTokenDoCookie(request)
        authenticationService.realizarLogout(tokenSessao!!, response)
        return ResponseEntity.ok<LoginResponseDTO?>(LoginResponseDTO(false, "Logout realizado com sucesso.", null))
    }

    companion object {
        private const val COOKIE_NAME = "session_token"
    }
}