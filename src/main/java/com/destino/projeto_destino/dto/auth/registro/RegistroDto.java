package com.destino.projeto_destino.dto.auth.registro;

import jakarta.validation.constraints.Size;

public record RegistroDto(
        String nome,
        String sobreNome,
        String cpf,
        String email,
        String telefone,

        @Size(min=8, max=50, message="A senha deve ter entre 8 e 50 caracteres")
        String senha
) {}
