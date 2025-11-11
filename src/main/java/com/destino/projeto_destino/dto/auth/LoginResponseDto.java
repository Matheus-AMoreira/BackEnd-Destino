package com.destino.projeto_destino.dto.auth;

import java.util.Optional;

public record LoginResponseDto(
        Boolean error,
        String mensagem,
        Optional<UserInfo> userInfo
) {
    public record UserInfo(
            String id,
            String nome
    ) { }
}
