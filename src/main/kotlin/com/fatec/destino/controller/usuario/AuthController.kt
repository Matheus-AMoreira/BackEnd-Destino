package com.fatec.destino.controller.usuario

import com.fatec.destino.dto.auth.login.LoginResponseDTO
import com.fatec.destino.dto.auth.login.LoginUsuarioDTO
import com.fatec.destino.dto.auth.registro.UsuarioRegistrationResponseDto
import com.fatec.destino.dto.auth.registro.UsuarioRegistroDTO
import com.fatec.destino.services.auth.AuthenticationService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/auth")
class AuthController(private val authenticationService: AuthenticationService) {
    companion object {
        private const val COOKIE_NAME = "session_token"
    }

    @PostMapping("/cadastrar")
    fun cadastrar(@Valid @RequestBody usuario: UsuarioRegistroDTO): ResponseEntity<UsuarioRegistrationResponseDto> {
        val resposta = authenticationService.cadastrarUsuario(usuario)

        // Decide o status: 201 (Created) se sucesso, 400 (Bad Request) se erro
        val status = if (resposta.erro) HttpStatus.BAD_REQUEST else HttpStatus.CREATED

        return ResponseEntity.status(status).body(resposta)
    }

    @PostMapping("/entrar")
    fun entrar(
        @Valid @RequestBody loginDto: LoginUsuarioDTO,
        response: HttpServletResponse
    ): ResponseEntity<LoginResponseDTO> {
        val resposta = authenticationService.realizarLogin(loginDto, response)

        val status = if (resposta.erro == true) HttpStatus.UNAUTHORIZED else HttpStatus.OK

        return ResponseEntity.status(status).body(resposta)
    }

    @PostMapping("/renovar-token")
    fun renovarToken(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<LoginResponseDTO> {
        val tokenSessao = extrairTokenDoCookie(request)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(LoginResponseDTO(true, "Cookie de sessão não encontrado.", null))

        val resposta = authenticationService.renovarToken(tokenSessao, response)

        val status = if (resposta.erro == true) HttpStatus.UNAUTHORIZED else HttpStatus.OK

        return ResponseEntity.status(status).body(resposta)
    }

    @PostMapping("/sair")
    fun sair(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<LoginResponseDTO> {
        val tokenSessao = extrairTokenDoCookie(request)

        // Se encontrou o cookie, realiza o logout
        if (tokenSessao != null) {
            authenticationService.realizarLogout(tokenSessao, response)
        }

        return ResponseEntity.ok(LoginResponseDTO(false, "Logout realizado com sucesso.", null))
    }

    // Método privado auxiliar simplificado
    private fun extrairTokenDoCookie(request: HttpServletRequest): String? {
        return request.cookies?.find { it.name == COOKIE_NAME }?.value
    }
}