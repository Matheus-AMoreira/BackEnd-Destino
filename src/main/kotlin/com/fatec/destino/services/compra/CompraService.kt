package com.fatec.destino.services.compra

import com.fatec.destino.dto.compra.CompraRequestDTO
import com.fatec.destino.dto.compra.CompraResponseDTO
import com.fatec.destino.dto.viagem.ViagemDetalhadaDTO
import com.fatec.destino.dto.viagem.ViagemResumoDTO
import com.fatec.destino.model.Compra
import com.fatec.destino.model.pacote.pacoteFoto.foto.Foto
import com.fatec.destino.repository.pacote.PacoteRepository
import com.fatec.destino.repository.usuario.CompraRepository
import com.fatec.destino.repository.usuario.UsuarioRepository
import com.fatec.destino.util.model.compra.Metodo
import com.fatec.destino.util.model.compra.Processador
import com.fatec.destino.util.model.compra.StatusCompra
import com.fatec.destino.util.model.pacote.PacoteStatus
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class CompraService(
    private val compraRepository : CompraRepository,
    private val usuarioRepository : UsuarioRepository,
    private val pacoteRepository : PacoteRepository
) {
    @Transactional
    fun processarCompra(dto: CompraRequestDTO): CompraResponseDTO {
        // 1. Buscar Usuário e Pacote (Correção: findByIdOrNull)
        val usuario = usuarioRepository.findByIdOrNull(dto.usuarioId)
            ?: throw RuntimeException("Usuário não encontrado")

        val pacote = pacoteRepository.findByIdOrNull(dto.pacoteId)
            ?: throw RuntimeException("Pacote não encontrado")

        // 2. Validar Disponibilidade
        if (pacote.disponibilidade <= 0) {
            return CompraResponseDTO(
                "Pacote esgotado!",
                compra = null
            )
        }

        // 3. Regras de Pagamento (PIX vs Cartão)
        var valorFinal = pacote.preco
        var parcelasFinais = dto.parcelas
        var metodoFinal = dto.metodo

        if (dto.processador == Processador.PIX) {
            valorFinal = valorFinal.multiply(BigDecimal.valueOf(0.95)) // 5% desconto
            parcelasFinais = 1
            metodoFinal = Metodo.VISTA
        } else if (parcelasFinais > 1) {
            metodoFinal = Metodo.PARCELADO
        }

        // 4. Criar a Compra
        val compra = Compra(
            dataCompra = Date(),
            metodo = metodoFinal,
            processadorPagamento = dto.processador,
            parcelas = parcelasFinais,
            valorFinal = valorFinal,
            usuario = usuario,
            pacote = pacote
        )

        compra.statusCompra = StatusCompra.ACEITO

        // 5. Atualizar Estoque e Salvar
        pacote.disponibilidade -= 1
        pacoteRepository.save(pacote)

        val compraRealizada = compraRepository.save(compra)

        return CompraResponseDTO("Compra realizada com sucesso", compraRealizada)
    }

    // Retorna List imutável e não nula
    fun listarViagensEmAndamentoDoUsuario(emailUsuario: String): List<ViagemResumoDTO> {
        val usuario = usuarioRepository.findByEmail(emailUsuario) ?: return emptyList()

        // Assumindo que o repositório retorna List<Compra> (ajuste se retornar null)
        val compras = compraRepository.findAllByUsuarioIdWhereStatusEmAdamento(
            usuario.id,
            PacoteStatus.EMANDAMENTO
        ) ?: emptyList()

        return compras.mapNotNull { compra -> // mapNotNull ignora nulos se houver
            compra?.let {
                ViagemResumoDTO(
                    id = it.id,
                    pacoteId = it.pacote.id,
                    nomePacote = it.pacote.nome,
                    descricao = it.pacote.descricao,
                    valor = it.valorFinal,
                    statusCompra = it.statusCompra,
                    dataPartida = it.pacote.inicio,
                    dataRetorno = it.pacote.fim,
                    imagemCapa = it.pacote.fotosDoPacote?.fotoDoPacote, // Nullable seguro
                    cidade = it.pacote.hotel.cidade,
                    estado = it.pacote.hotel.cidade.estado.sigla
                )
            }
        }
    }

    fun listarViagensConcluidasDoUsuarios(emailUsuario: String): List<ViagemResumoDTO> {
        val usuario = usuarioRepository.findByEmail(emailUsuario) ?: return emptyList()

        val compras = compraRepository.findAllByUsuarioIdWhereStatusConcluido(
            usuario.id,
            PacoteStatus.CONCLUIDO
        ) ?: emptyList()

        return compras.mapNotNull { compra ->
            compra?.let {
                ViagemResumoDTO(
                    id = it.id,
                    pacoteId = it.pacote.id,
                    nomePacote = it.pacote.nome,
                    descricao = it.pacote.descricao,
                    valor = it.valorFinal,
                    statusCompra = it.statusCompra,
                    dataPartida = it.pacote.inicio,
                    dataRetorno = it.pacote.fim,
                    imagemCapa = it.pacote.fotosDoPacote?.fotoDoPacote,
                    cidade = it.pacote.hotel.cidade,
                    estado = it.pacote.hotel.cidade.estado.sigla
                )
            }
        }
    }

    fun buscarDetalhesViagem(compraId: Long, emailUsuario: String): ViagemDetalhadaDTO {
        // FindBy retorna Compra? (nullable) ou Optional? Ajustado para nullable (estilo Kotlin)
        // Se seu repo retorna Optional, use .orElse(null) antes do elvis ?:
        val compra = compraRepository.findByIdAndUsuarioEmail(compraId, emailUsuario)
            ?: throw RuntimeException("Viagem não encontrada ou acesso negado")

        return ViagemDetalhadaDTO(
            id = compra.id,
            nomePacote = compra.pacote.nome,
            descricao = compra.pacote.descricao,
            valor = compra.valorFinal,
            statusCompra = compra.statusCompra.toString(),
            dataPartida = compra.pacote.inicio,
            dataRetorno = compra.pacote.fim,
            dataCompra = compra.dataCompra,
            numeroReserva = "RES-${compra.id}",
            imagemPrincipal = compra.pacote.fotosDoPacote?.fotoDoPacote,
            galeria = compra.pacote.fotosDoPacote?.fotos as List<Foto?>?,
            inclusoes = compra.pacote.tags,
            nomeHotel = compra.pacote.hotel.nome,
            tipoTransporte = compra.pacote.transporte.meio
        )
    }
}