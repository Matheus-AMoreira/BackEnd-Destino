package com.fatec.destino.util.model.usuario.perfil;

import java.util.List;

public enum UserRole {
    ADMINISTRADOR(List.of(
          UsuarioAuthority.DELETAR_USUARIO
    )),
    FUNCIONARIO(List.of(
            UsuarioAuthority.CRIAR_PACOTE
    )),
    USUARIO(List.of(
            UsuarioAuthority.EDICAO_PERFIL
    ));

    private final List<UsuarioAuthority> authorities;

    UserRole(List<UsuarioAuthority> authorities) {
        this.authorities = authorities;
    }

    public List<UsuarioAuthority> getAuthorities() {
        return authorities;
    }
}