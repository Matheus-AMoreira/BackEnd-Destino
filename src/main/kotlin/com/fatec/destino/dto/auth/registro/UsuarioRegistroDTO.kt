package com.fatec.destino.dto.auth.registro

import com.fatec.destino.util.model.usuario.cpf.Cpf
import com.fatec.destino.util.model.usuario.telefone.Telefone
import jakarta.validation.constraints.Size

data class UsuarioRegistroDTO(
    val nome: String,
    val sobreNome: String,
    val cpf: Cpf,
    val email: String,
    val telefone: Telefone?,
    val senha: @Size(min = 8, max = 50, message = "A senha deve ter entre 8 e 50 caracteres") String?
)