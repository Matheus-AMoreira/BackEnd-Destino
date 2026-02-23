package com.fatec.destino.model.pacote

import com.fasterxml.jackson.annotation.JsonFormat
import com.fatec.destino.dto.pacote.PacoteRegistroDTO
import com.fatec.destino.model.pacote.hotel.Hotel
import com.fatec.destino.model.pacote.pacoteFoto.PacoteFoto
import com.fatec.destino.model.pacote.transporte.Transporte
import com.fatec.destino.model.usuario.Usuario
import com.fatec.destino.util.model.pacote.PacoteStatus
import com.fatec.destino.util.model.pacote.StringListConverter
import com.fatec.destino.model.pacote.tag.Tag
import com.fatec.destino.model.viagem.Viagem
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
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

    @ManyToMany
    @JoinTable(
        name = "pac_pacote_tag",
        joinColumns = [JoinColumn(name = "PAC_ID")],
        inverseJoinColumns = [JoinColumn(name = "TAG_ID")]
    )
    var tags: Set<Tag> = emptySet(),

    @Column(name = "PAC_PRECO", precision = 10, scale = 2, nullable = false)
    var preco: BigDecimal,

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

    @OneToMany(mappedBy = "pacote", cascade = [CascadeType.ALL], orphanRemoval = true)
    var viagens: Set<Viagem> = emptySet()

    fun atualizarDados(
        dto: PacoteRegistroDTO,
        hotel: Hotel,
        transporte: Transporte,
        funcionario: Usuario,
        foto: PacoteFoto?,
        tags: Set<Tag>
    ) {
        this.nome = dto.nome
        this.descricao = dto.descricao
        this.preco = dto.preco.toBigDecimal()
        this.hotel = hotel
        this.transporte = transporte
        this.funcionario = funcionario
        this.fotosDoPacote = foto
        this.tags = tags
    }

    val precoFormatado: String
        get() = String.format("%.2f", preco)
}