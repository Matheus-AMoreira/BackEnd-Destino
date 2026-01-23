package com.fatec.destino.controller.testar

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/test")
class TestController {
    @GetMapping("/publico")
    fun rotaPublica(): String {
        return "Olá! Esta é uma rota pública."
    }

    @GetMapping("/privado/usuario")
    @PreAuthorize("hasRole('ROLE_USUARIO')")
    fun rotaUsuario(): String {
        return "Essa rota só usuário, funcionário e administrador tem acesso"
    }

    @GetMapping("/privado/funcionario")
    @PreAuthorize("hasRole('ROLE_FUNCIONARIO')")
    fun rotaPrivada(): String {
        return "Informação Secreta. Você acessou com um token de 'funcionário' ou 'adm' válido!"
    }

    @GetMapping("/privado/admin")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    fun rotaAdmin(): String {
        return "Dados Ultra Secretos. Acesso concedido apenas para administradores!"
    }
}