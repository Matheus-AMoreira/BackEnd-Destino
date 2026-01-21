package com.fatec.destino.model.pacote.pacoteFoto.foto

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "fot_foto")
class Foto (
    @Column(name = "FOT_NOME", length = 100, nullable = false)
    var nome: String? = null,
    
    @Column(name = "FOT_URL", nullable = false)
    var url: String? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FOT_ID", nullable = false)
    var id: Long? = null
}