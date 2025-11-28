package com.destino.projeto_destino.controller.testar;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/publico")
    public String rotaPublica() {
        return "Olá! Esta é uma rota pública.";
    }

    ;

    @GetMapping("/privado/usuario")
    @PreAuthorize("hasRole('ROLE_USUARIO')")
    public String rotaUsuario() {
        return "Essa rota só usuário, funcionário e administrador tem acesso";
    }

    ;

    @GetMapping("/privado/funcionario")
    @PreAuthorize("hasRole('ROLE_FUNCIONARIO')")
    public String rotaPrivada() {
        return "Informação Secreta. Você acessou com um token de 'funcionário' ou 'adm' válido!";
    }

    @GetMapping("/privado/admin")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public String rotaAdmin() {
        return "Dados Ultra Secretos. Acesso concedido apenas para administradores!";
    }
}
