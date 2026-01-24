package com.fatec.destino.controller.usuario

import com.fatec.destino.dto.auth.validar.ValidarResponseDTO
import com.fatec.destino.dto.usuario.UsuarioDTO
import com.fatec.destino.model.usuario.Usuario
import com.fatec.destino.services.usuario.UsuarioService
import org.springframework.http.ResponseEntity
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/usuario")
@EnableMethodSecurity(prePostEnabled = true)
class UsuarioController(
    private val usuarioService: UsuarioService
) {
    @GetMapping("/invalidos")
    fun inValiduser(): ResponseEntity<List<UsuarioDTO>> {
        return usuarioService.inValidUsers()
    }

    @PatchMapping("/validar/{id}")
    fun validUser(@PathVariable id: UUID?): ResponseEntity<ValidarResponseDTO> {
        if (id == null) {
            return ResponseEntity.badRequest().body(ValidarResponseDTO(true, "Precisa enviar um id"))
        }
        return usuarioService.validar(id)
    }

    @GetMapping
    fun buscarUsuarios(): ResponseEntity<List<Usuario?>> {
        return ResponseEntity.ok().body(usuarioService.buscarUsuarios())
    }
}