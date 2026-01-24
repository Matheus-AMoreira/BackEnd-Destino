package com.fatec.destino.model.pacote.hotel

import com.fatec.destino.model.pacote.hotel.cidade.Cidade
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "hot_hotel")
class Hotel(
    @Column(name = "HOT_NOME", length = 50, nullable = false)
    var nome: String? = null,

    @Column(name = "HOT_ENDERECO", length = 100, nullable = false)
    var endereco: String? = null,

    @Column(name = "HOT_diária", precision = 10, scale = 2, nullable = false)
    var diaria: Int = 0,

    @ManyToOne
    @JoinColumn(name = "cid_id", nullable = false)
    var cidade: Cidade
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HOT_ID", nullable = false)
    var id: Long? = null
}