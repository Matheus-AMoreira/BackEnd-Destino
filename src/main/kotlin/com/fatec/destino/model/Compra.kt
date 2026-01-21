package com.fatec.destino.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fatec.destino.model.pacote.Pacote
import com.fatec.destino.model.usuario.Usuario
import com.fatec.destino.util.model.compra.Metodo
import com.fatec.destino.util.model.compra.Processador
import com.fatec.destino.util.model.compra.StatusCompra
import jakarta.persistence.Column
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
import java.util.*

@Entity
@Table(name = "com_compra")
class Compra (
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "COM_DATA_COMPRA", nullable = false)
    var dataCompra: Date,

    @Enumerated(EnumType.STRING)
    @Column(name = "COM_STATUS", nullable = false, length = 50)
    var statusCompra: StatusCompra = StatusCompra.PENDENTE,

    @Enumerated(EnumType.STRING)
    @Column(name = "COM_METODO", nullable = false, length = 50)
    var metodo: Metodo,

    @Enumerated(EnumType.STRING)
    @Column(name = "COM_PROCESSADOR", nullable = false, length = 50)
    var processadorPagamento: Processador,

    @Column(name = "COM_PARCELAS", nullable = false)
    var parcelas: Int = 1,

    @Column(name = "COM_VALOR_FINAL", nullable = false, precision = 10, scale = 2)
    var valorFinal: BigDecimal,

    // Chaves Estrangeiras
    @ManyToOne
    @JoinColumn(name = "USU_ID", referencedColumnName = "USU_ID", nullable = false)
    val usuario: Usuario,

    @ManyToOne
    @JoinColumn(name = "PAC_ID", referencedColumnName = "PAC_ID", nullable = false)
    val pacote: Pacote
) {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "COM_ID", nullable = false)
    var id: UUID? = null
}