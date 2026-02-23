package com.fatec.destino.model.viagem

import com.fasterxml.jackson.annotation.JsonFormat
import com.fatec.destino.model.pacote.Pacote
import com.fatec.destino.util.model.pacote.PacoteStatus
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "via_viagem")
class Viagem(
    @ManyToOne
    @JoinColumn(name = "PAC_ID", referencedColumnName = "PAC_ID", nullable = false)
    var pacote: Pacote,

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "VIA_DATA_INICIO", nullable = false)
    var inicio: LocalDate,

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "VIA_DATA_FIM", nullable = false)
    var fim: LocalDate,

    @Column(name = "VIA_DISPONIBILIDADE", nullable = false)
    var disponibilidade: Int = 0,

    @Column(name = "VIA_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: PacoteStatus = PacoteStatus.EMANDAMENTO
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VIA_ID", nullable = false)
    var id: Long? = null

    fun recalcularStatus() {
        this.status = when {
            this.status == PacoteStatus.CANCELADO -> PacoteStatus.CANCELADO
            this.fim.isBefore(LocalDate.now()) -> PacoteStatus.CONCLUIDO
            else -> PacoteStatus.EMANDAMENTO
        }
    }
}
