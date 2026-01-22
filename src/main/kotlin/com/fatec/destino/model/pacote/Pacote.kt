package com.fatec.destino.model.pacote

import com.fasterxml.jackson.annotation.JsonFormat
import com.fatec.destino.dto.pacote.PacoteRegistroDTO
import com.fatec.destino.model.pacote.hotel.Hotel
import com.fatec.destino.model.pacote.pacoteFoto.PacoteFoto
import com.fatec.destino.model.pacote.transporte.Transporte
import com.fatec.destino.model.usuario.Usuario
import com.fatec.destino.util.model.pacote.PacoteStatus
import com.fatec.destino.util.model.pacote.StringListConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Entity
@Table(name = "pac_pacote")
class Pacote (
    @Column(name = "PAC_NOME", length = 100, nullable = false)
    var nome: String,

    @Column(name = "PAC_DESCRICAO", nullable = false, columnDefinition = "TEXT")
    var descricao: String,

    @Column(name = "PAC_ITENS")
    @Convert(converter = StringListConverter::class)
    var tags: ArrayList<String>? = null,

    @Column(name = "PAC_PRECO", precision = 10, scale = 2, nullable = false)
    var preco: BigDecimal,

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "PAC_DATA_INICIO_VIAGEM", nullable = false)
    var inicio: LocalDate,

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "PAC_DATA_FIM_VIAGEM", nullable = false)
    var fim: LocalDate,

    @Column(name = "PAC_DISPONIBILIDADE", nullable = false)
    var disponibilidade: Int = 0,

    @Column(name = "PAC_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: PacoteStatus = PacoteStatus.EMANDAMENTO,

    // Relacionamentos
    @ManyToOne
    @JoinColumn(name = "TRA_ID", referencedColumnName = "TRA_ID", nullable = false)
    var transporte: Transporte,

    @ManyToOne
    @JoinColumn(name = "HOT_ID", referencedColumnName = "HOT_ID", nullable = false)
    var hotel: Hotel,

    @ManyToOne
    @JoinColumn(name = "USU_ID", referencedColumnName = "USU_ID", nullable = false)
    var funcionario: Usuario,

    @ManyToOne
    @JoinColumn(name = "pcf_id", referencedColumnName = "pcf_id", nullable = true)
    var fotosDoPacote: PacoteFoto? = null
){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAC_ID", nullable = false)
    var id: Long? = null

    fun atualizarDados(
        dto: PacoteRegistroDTO,
        hotel: Hotel,
        transporte: Transporte,
        funcionario: Usuario,
        foto: PacoteFoto?
    ) {
        // Regra de Negócio: Validar preço antes de atribuir
        val dias = ChronoUnit.DAYS.between(dto.inicio, dto.fim).coerceAtLeast(1).toBigDecimal()
        val custoMinimo = (hotel.diaria.toBigDecimal() * dias) + transporte.preco.toBigDecimal()

        if (dto.preco < custoMinimo) {
            throw IllegalArgumentException("Preço insuficiente! O custo base é $custoMinimo")
        }

        // Se passar na validação, atribui os valores
        this.nome = dto.nome
        this.descricao = dto.descricao
        this.preco = dto.preco
        this.inicio = dto.inicio
        this.fim = dto.fim
        this.hotel = hotel
        this.transporte = transporte
        this.funcionario = funcionario
        this.fotosDoPacote = foto

        // A própria classe decide o status
        recalcularStatus()
    }

    private fun recalcularStatus() {
        this.status = when {
            this.status == PacoteStatus.CANCELADO -> PacoteStatus.CANCELADO
            this.fim.isBefore(LocalDate.now()) -> PacoteStatus.CONCLUIDO
            else -> PacoteStatus.EMANDAMENTO
        }
    }

    val precoFormatado: String
        get() = String.format("%.2f", preco)
}