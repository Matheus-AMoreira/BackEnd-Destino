package com.fatec.destino.dto.auth.login

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LoginResponseDTO(
    val erro: Boolean?,
    val mensagem: String?,
    val dados: UserInfo?
) {
    data class UserInfo(
        val id: UUID?,
        val nomeCompleto: String?,
        val email: String?,
        val perfil: String?,
        val accessToken: String?,
        val expiracaoEmSegundos: Long?
    )
}
