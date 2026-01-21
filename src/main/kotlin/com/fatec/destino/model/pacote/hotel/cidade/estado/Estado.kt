package com.fatec.destino.model.pacote.hotel.cidade.estado

import com.fatec.destino.model.pacote.hotel.cidade.estado.regiao.Regiao
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "est_estado")
class Estado(
    @Id
    @Column(name = "est_id", nullable = false)
    val id: Long,
    @Column(name = "est_sigla", length = 2, nullable = false)
    val sigla: String,
    @Column(name = "est_nome", length = 100, nullable = false)
    val nome: String,
    @ManyToOne
    @JoinColumn(name = "reg_id")
    val regiao: Regiao
) { }