package com.destino.projeto_destino.dto;

public record LoginResponseDto(
        String token,
        long expiresIn
) { }
