package com.fatec.destino.model.pacote.hotel.cidade

import com.fatec.destino.model.pacote.hotel.cidade.estado.Estado
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "cid_cidade")
class Cidade (
    @Id
    @Column(name = "cid_id", nullable = false)
    var id: Long,

    @Column(name = "cid_nome", length = 40, nullable = false)
    var nome: String,

    @ManyToOne
    @JoinColumn(name = "est_id", nullable = false)
    var estado: Estado
)