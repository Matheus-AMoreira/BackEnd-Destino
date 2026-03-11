package org.tech6.dto.auth.registro;

import org.tech6.util.model.usuario.Cpf.Cpf;
import org.tech6.util.model.usuario.Telefone.Telefone;
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
