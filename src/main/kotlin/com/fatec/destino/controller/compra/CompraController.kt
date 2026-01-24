package com.fatec.destino.controller.compra

import com.fatec.destino.dto.compra.CompraRequestDTO
import com.fatec.destino.dto.compra.CompraResponseDTO
import com.fatec.destino.dto.viagem.ViagemDetalhadaDTO
import com.fatec.destino.dto.viagem.ViagemResumoDTO
import com.fatec.destino.services.compra.CompraService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/compra")
class CompraController(private val compraService: CompraService) {

    @GetMapping("/andamento")
    fun listarViagensEmAndamento(): ResponseEntity<List<ViagemResumoDTO>> {
        val email = getEmailAutenticado() ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        return ResponseEntity.ok(compraService.listarViagensEmAndamentoDoUsuario(email))
    }

    @GetMapping("/concluidas")
    fun listarViagensConcluidas(): ResponseEntity<List<ViagemResumoDTO>> {
        val email = getEmailAutenticado() ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        return ResponseEntity.ok(compraService.listarViagensConcluidasDoUsuarios(email))
    }

    @GetMapping("/{id}")
    fun detalharViagem(@PathVariable id: Long): ResponseEntity<ViagemDetalhadaDTO> {
        val email = getEmailAutenticado() ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        return try {
            val detalhes = compraService.buscarDetalhesViagem(id, email)
            ResponseEntity.ok(detalhes)
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun realizarCompra(@RequestBody dto: CompraRequestDTO): ResponseEntity<CompraResponseDTO> {
        // Aqui não precisa de email do token pois o DTO já traz o usuarioId (conforme sua lógica original)
        // Se quiser validar se o ID do usuário bate com o token, faça aqui.
        return ResponseEntity.ok(compraService.processarCompra(dto))
    }

    // Método auxiliar para pegar o email com segurança
    private fun getEmailAutenticado(): String? {
        val auth = SecurityContextHolder.getContext().authentication
        return if (auth != null && auth.isAuthenticated && auth.name != "anonymousUser") {
            auth.name
        } else {
            null
        }
    }
}