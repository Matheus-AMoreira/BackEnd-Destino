package com.fatec.destino.util.model.usuario.perfil

import org.springframework.security.core.GrantedAuthority

enum class UserRole : GrantedAuthority {
    ADMINISTRADOR,
    FUNCIONARIO,
    USUARIO;

    override fun getAuthority(): String {
        return "ROLE_$name"
    }
}