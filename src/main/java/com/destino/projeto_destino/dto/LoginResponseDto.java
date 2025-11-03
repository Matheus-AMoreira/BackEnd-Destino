package com.destino.projeto_destino.dto;

import java.util.Optional;

public record LoginResponseDto(
        Boolean error,
        String message,
        Optional<UserInfo> userInfo
) {
    public record UserInfo(
            String id,
            String nome
    ) { }
}
