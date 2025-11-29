package com.destino.projeto_destino.dto.auth.login;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginResponseDTO(
        Boolean erro,
        String mensagem,
        UserInfo dados
) {
    public record UserInfo(
            UUID id,
            String nomeCompleto,
            String email,
            String perfil,
            String accessToken,
            Long expiracaoEmSegundos
    ) {
    }
}
