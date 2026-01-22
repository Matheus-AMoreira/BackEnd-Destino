package com.fatec.destino.services.usuario

import com.fatec.destino.dto.auth.validar.ValidarResponseDTO
import com.fatec.destino.dto.usuario.UsuarioDTO
import com.fatec.destino.model.usuario.Usuario
import com.fatec.destino.repository.usuario.UsuarioRepository
import jakarta.transaction.Transactional
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class UsuarioService(private val userRepository: UsuarioRepository) {
    fun buscarUsuarios(): Iterable<Usuario?> {
        return userRepository.findAll()
    }

    fun inValidUsers(): ResponseEntity<MutableList<UsuarioDTO?>?> {
        val usuarios = userRepository.findByValidoFalse()
        val users = usuarios!!.stream().map<UsuarioDTO?> { usuario: Usuario? ->
            UsuarioDTO(
                usuario!!.id.toString(),
                usuario.nome + " " + usuario.sobreNome,
                usuario.cpf.getValorFormatado(),
                usuario.email,
                usuario.telefone!!.valorFormatado,
                usuario.perfil.name,
                if (usuario.valido == true) "Sim" else "Não",
                usuario.cadastro
            )
        }.toList()
        return ResponseEntity.ok().body<MutableList<UsuarioDTO?>?>(users)
    }

    @Transactional
    fun validar(id: UUID?): ResponseEntity<ValidarResponseDTO?> {
        val linhasAfetadas = userRepository.validarUsuario(id)

        if (linhasAfetadas == 0) {
            return ResponseEntity.ok()
                .body<ValidarResponseDTO?>(ValidarResponseDTO(true, "Não existe usuario com id: " + id))
        }

        return ResponseEntity.ok()
            .body<ValidarResponseDTO?>(ValidarResponseDTO(false, "Usuário atualizado com sucesso!"))
    }
}
