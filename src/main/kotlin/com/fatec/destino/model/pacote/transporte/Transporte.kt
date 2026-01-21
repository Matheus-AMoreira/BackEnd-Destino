package com.fatec.destino.model.pacote.transporte

import com.fatec.destino.util.model.transporte.Meio
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "tra_transporte")
class Transporte  (
    @Column(name = "TRA_EMPRESA", length = 100, nullable = false)
    var empresa: String? = null,

    @Column(name = "TRA_MEIO", nullable = false)
    var meio: Meio? = null,

    @Column(name = "TRA_PRECO", precision = 10, scale = 2, nullable = false)
    var preco: Int = 0
){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRA_ID", nullable = false)
    var id: Long? = null
}