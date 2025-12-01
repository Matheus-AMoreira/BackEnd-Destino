package com.destino.projeto_destino.dto.usuario;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public record UsuarioDTO(
        String id,
        String nome,
        String cpf,
        String email,
        String telefone,
        String perfil,
        String valido,

        @JsonFormat(pattern = "dd-MM-yyyy")
        Date cadastro
) {
}
