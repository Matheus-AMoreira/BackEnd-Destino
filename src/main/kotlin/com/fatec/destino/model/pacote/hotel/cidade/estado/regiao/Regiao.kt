package com.fatec.destino.model.pacote.hotel.cidade.estado.regiao

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "reg_regiao")
class Regiao(
    @Id
    @Column(name = "reg_id", nullable = false)
    var id: Long? = null,

    @Column(name = "reg_sigla", length = 2, nullable = false)
    var sigla: String? = null,

    @Column(name = "reg_nome", length = 12, nullable = false)
    var nome: String? = null,
) { }