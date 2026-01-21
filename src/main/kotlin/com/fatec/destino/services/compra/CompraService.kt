package com.fatec.destino.services.compra

import com.fatec.destino.dto.compra.CompraRequestDTO
import com.fatec.destino.dto.compra.CompraResponseDTO
import com.fatec.destino.dto.compra.ViagemDetalhadaDTO
import com.fatec.destino.dto.compra.ViagemResumoDTO
import com.fatec.destino.model.Compra
import com.fatec.destino.repository.pacote.PacoteRepository
import com.fatec.destino.repository.usuario.CompraRepository
import com.fatec.destino.repository.usuario.UsuarioRepository
import com.fatec.destino.util.model.compra.Metodo
import com.fatec.destino.util.model.compra.Processador
import com.fatec.destino.util.model.compra.StatusCompra
import com.fatec.destino.util.model.pacote.PacoteStatus
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*
import java.util.function.Supplier

@Service
class CompraService(
    private val compraRepository : CompraRepository,
    private val usuarioRepository : UsuarioRepository,
    private val pacoteRepository : PacoteRepository
) {
    @Transactional
    fun processarCompra(dto: CompraRequestDTO): CompraResponseDTO {
        // 1. Buscar Usuário e Pacote
        val usuario = usuarioRepository.findById(dto.usuarioId)
            .orElseThrow<RuntimeException?>(Supplier { RuntimeException("Usuário não encontrado") })

        val pacote = pacoteRepository.findById(dto.pacoteId)
            .orElseThrow<RuntimeException?>(Supplier { RuntimeException("Pacote não encontrado") })

        // 2. Validar Disponibilidade
        if (pacote.disponibilidade <= 0) {
            return CompraResponseDTO("Pacote esgotado!", Optional.empty<Compra?>())
        }

        // 3. Regras de Pagamento (PIX vs Cartão)
        var valorFinal = pacote.preco
        var parcelasfinais = dto.parcelas
        var metodoFinal = dto.metodo

        if (dto.processador == Processador.PIX) {
            valorFinal = valorFinal.multiply(BigDecimal.valueOf(0.95)) // 5% desconto
            parcelasfinais = 1
            metodoFinal = Metodo.VISTA
        } else {
            if (parcelasfinais > 1) {
                metodoFinal = Metodo.PARCELADO
            }
        }

        // 4. Criar a Compra
        val compra = Compra(
            dataCompra = Date(),
            metodo = metodoFinal,
            processadorPagamento = dto.processador,
            parcelas = parcelasfinais,
            valorFinal = valorFinal,
            usuario = usuario,
            pacote = pacote
        )

        // Simulação: Aprovação imediata
        compra.statusCompra = StatusCompra.ACEITO

        // 5. Atualizar Estoque do Pacote
        pacote.disponibilidade = pacote.disponibilidade - 1
        pacoteRepository.save(pacote)

        val compraRealizada: Compra = compraRepository.save<Compra?>(compra)!!

        return CompraResponseDTO("Compra realizada com sucesso", Optional.of<Compra?>(compraRealizada))
    }

    fun listarViagensEmAndamentoDoUsuario(emailUsuario : String) : List<ViagemResumoDTO> {
        val usuario = usuarioRepository.findByEmail(emailUsuario).orElseThrow { RuntimeException("Usuário não encontrado") }

        val compras = compraRepository.findAllByUsuarioIdWhereStatusEmAdamento(usuario.id, PacoteStatus.EMANDAMENTO)

        //return compras.stream().map(this::converterParaResumoDTO).collect(Collectors.toList());
        return compras.map { compra -> ViagemResumoDTO(
            id = compra.id,
            pacoteId = compra.pacote.id,
            nomePacote = compra.pacote.nome,
            descricao = compra.pacote.descricao,
            valor = compra.valorFinal,
            statusCompra = compra.statusCompra,
            dataPartida = compra.pacote.inicio,
            dataRetorno = compra.pacote.fim,
            imagemCapa = compra.pacote.fotosDoPacote?.fotoDoPacote,
            cidade = compra.pacote.hotel.cidade,
            estado = compra.pacote.hotel.cidade?.estado?.sigla
        ) }
    }

    fun listarViagensConcluidasDoUsuarios(emailUsuario: String): List<ViagemResumoDTO> {
        val usuario = usuarioRepository.findByEmail(emailUsuario)
            .orElseThrow { RuntimeException("Usuário não encontrado") }

        val compras = compraRepository.findAllByUsuarioIdWhereStatusConcluido(
            usuario.id,
            PacoteStatus.CONCLUIDO
        )

        return compras.map { compra -> ViagemResumoDTO(
            id = compra.id,
            pacoteId = compra.pacote.id,
            nomePacote = compra.pacote.nome,
            descricao = compra.pacote.descricao,
            valor = compra.valorFinal,
            statusCompra = compra.statusCompra,
            dataPartida = compra.pacote.inicio,
            dataRetorno = compra.pacote.fim,
            imagemCapa = compra.pacote.fotosDoPacote?.fotoDoPacote,
            cidade = compra.pacote.hotel.cidade,
            estado = compra.pacote.hotel.cidade?.estado?.sigla
        ) }
    }

    fun buscarDetalhesViagem(compraId: Long, emailUsuario: String): ViagemDetalhadaDTO {
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
            galeria = compra.pacote.fotosDoPacote?.fotos,
            inclusoes = compra.pacote.tags,
            nomeHotel = compra.pacote.hotel.nome,
            tipoTransporte = compra.pacote.transporte.meio
        )
    }
}