package com.fatec.destino.dto.usuario

import java.util.Date

data class UsuarioDTO (
    var id: String,
    var nome: String,
    var cpf: String?,
    var email: String,
    var telefone: String?,
    var perfil: String,
    var valido: String,
    var cadastro: Date?
)