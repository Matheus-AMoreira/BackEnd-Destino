package com.fatec.destino.dto.auth.registro;

import com.fatec.destino.util.model.usuario.Cpf.Cpf;
import com.fatec.destino.util.model.usuario.Telefone.Telefone;
import jakarta.validation.constraints.Size;

public record RegistroDto(
        String nome,
        String sobreNome,
        Cpf cpf,
        String email,
        Telefone telefone,

        @Size(min=8, max=50, message="A senha deve ter entre 8 e 50 caracteres")
        String senha

) {}
