package com.fatec.destino.services.compra

import com.fatec.destino.dto.compra.CompraRequestDTO
import com.fatec.destino.dto.compra.CompraResponseDTO
import com.fatec.destino.dto.viagem.ViagemDTO
import com.fatec.destino.model.Compra
import com.fatec.destino.model.pacote.pacoteFoto.foto.Foto
import com.fatec.destino.repository.viagem.ViagemRepository
import com.fatec.destino.repository.usuario.CompraRepository
import com.fatec.destino.repository.usuario.UsuarioRepository
import com.fatec.destino.util.model.compra.Metodo
import com.fatec.destino.util.model.compra.Processador
import com.fatec.destino.util.model.compra.StatusCompra
import com.fatec.destino.util.model.pacote.PacoteStatus
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class CompraService(
    private val compraRepository : CompraRepository,
    private val usuarioRepository : UsuarioRepository,
    private val viagemRepository : ViagemRepository
) {
    @Transactional
    fun processarCompra(dto: CompraRequestDTO): CompraResponseDTO {
        val usuario = usuarioRepository.findByIdOrNull(dto.usuarioId)
            ?: throw RuntimeException("Usuário não encontrado")

        val viagem = viagemRepository.findByIdOrNull(dto.viagemId)
            ?: throw RuntimeException("Viagem não encontrada")

        if (viagem.disponibilidade <= 0) {
            return CompraResponseDTO(
                "Viagem esgotada!",
                compra = null
            )
        }

        var valorFinal = viagem.pacote.preco
        var parcelasFinais = dto.parcelas
        var metodoFinal = dto.metodo

        if (dto.processador == Processador.PIX) {
            valorFinal = valorFinal.multiply(BigDecimal.valueOf(0.95))
            parcelasFinais = 1
            metodoFinal = Metodo.VISTA
        } else if (parcelasFinais > 1) {
            metodoFinal = Metodo.PARCELADO
        }

        val compra = Compra(
            dataCompra = Date(),
            metodo = metodoFinal,
            processadorPagamento = dto.processador,
            parcelas = parcelasFinais,
            valorFinal = valorFinal,
            usuario = usuario,
            viagem = viagem
        )

        compra.statusCompra = StatusCompra.ACEITO

        viagem.disponibilidade -= 1
        viagemRepository.save(viagem)

        val compraRealizada = compraRepository.save(compra)

        return CompraResponseDTO("Compra realizada com sucesso", compraRealizada)
    }

    fun listarViagensEmAndamentoDoUsuario(emailUsuario: String): List<ViagemDTO> {
        val usuario = usuarioRepository.findByEmail(emailUsuario) ?: return emptyList()

        val compras = compraRepository.findAllByUsuarioIdWhereStatusEmAdamento(
            usuario.id,
            PacoteStatus.EMANDAMENTO
        ) ?: emptyList()

        return compras.mapNotNull { it?.toDTO() }
    }

    fun listarViagensConcluidasDoUsuarios(emailUsuario: String): List<ViagemDTO> {
        val usuario = usuarioRepository.findByEmail(emailUsuario) ?: return emptyList()

        val compras = compraRepository.findAllByUsuarioIdWhereStatusConcluido(
            usuario.id,
            PacoteStatus.CONCLUIDO
        ) ?: emptyList()

        return compras.mapNotNull { it?.toDTO() }
    }

    fun buscarDetalhesViagem(compraId: Long, emailUsuario: String): ViagemDTO {
        val compra = compraRepository.findByIdAndUsuarioEmail(compraId, emailUsuario)
            ?: throw RuntimeException("Viagem não encontrada ou acesso negado")

        return ViagemDTO(
            id = compra.id,
            nome = compra.viagem.pacote.nome,
            descricao = compra.viagem.pacote.descricao,
            valor = compra.valorFinal,
            statusCompra = compra.statusCompra,
            inicio = compra.viagem.inicio,
            fim = compra.viagem.fim,
            dataCompra = compra.dataCompra,
            numeroReserva = "RES-${compra.id}",
            imagemCapa = compra.viagem.pacote.fotosDoPacote?.fotoDoPacote,
            galeria = compra.viagem.pacote.fotosDoPacote?.fotos as List<Foto?>?,
            tags = compra.viagem.pacote.tags.map { it.nome },
            hotel = compra.viagem.pacote.hotel.nome,
            transporte = compra.viagem.pacote.transporte.meio
        )
    }

    private fun Compra.toDTO() = ViagemDTO(
        id = id,
        pacoteId = viagem.pacote.id,
        nome = viagem.pacote.nome,
        descricao = viagem.pacote.descricao,
        valor = valorFinal,
        statusCompra = statusCompra,
        inicio = viagem.inicio,
        fim = viagem.fim,
        imagemCapa = viagem.pacote.fotosDoPacote?.fotoDoPacote,
        cidade = viagem.pacote.hotel.cidade,
        estado = viagem.pacote.hotel.cidade.estado.sigla
    )
}
