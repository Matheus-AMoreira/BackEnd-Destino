package com.destino.projeto_destino.dto;

import com.destino.projeto_destino.util.usuario.perfil.UserRole;

import java.util.Date;
import java.util.UUID;

public record UsuarioDTO(
        UUID id,
        String nome,
        String cpf,
        String email,
        String telefone,
        UserRole perfil,
        Boolean valido,
        Date atualização,
        Date cadastro
) {
}
