package com.fatec.destino.services.usuario

import com.fatec.destino.dto.auth.validar.ValidarResponseDTO
import com.fatec.destino.dto.usuario.UsuarioDTO
import com.fatec.destino.model.usuario.Usuario
import com.fatec.destino.repository.usuario.UsuarioRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class UsuarioService(private val userRepository: UsuarioRepository) {
    fun buscarUsuarios(): List<Usuario?> {
        return userRepository.findAll().orEmpty()
    }

    fun inValidUsers(): ResponseEntity<List<UsuarioDTO>> {
        val usuarios = userRepository.findByValidoFalse() ?: emptyList()

        val dtos = usuarios.filterNotNull().map { u ->
            UsuarioDTO(
                u.id.toString(),
                "${u.nome} ${u.sobreNome}",
                u.cpf.getvalorFormatado(),
                u.email,
                u.telefone?.valorFormatado,
                u.perfil.name,
                if (u.valido) "Sim" else "Não",
                u.cadastro
            )
        }
        return ResponseEntity.ok(dtos)
    }

    @Transactional
    fun validar(id: UUID): ResponseEntity<ValidarResponseDTO> {
        val linhasAfetadas = userRepository.validarUsuario(id)

        if (linhasAfetadas == 0) {
            return ResponseEntity.ok()
                .body(ValidarResponseDTO(true, "Não existe usuario com id: $id"))
        }

        return ResponseEntity.ok()
            .body(ValidarResponseDTO(false, "Usuário atualizado com sucesso!"))
    }
}
