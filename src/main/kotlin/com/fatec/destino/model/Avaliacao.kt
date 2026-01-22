package com.fatec.destino.model

import com.fatec.destino.model.pacote.Pacote
import com.fatec.destino.model.usuario.Usuario
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "ava_avaliacao")
class Avaliacao (
    @Column(name = "AVA_NOTA", nullable = false)
    var nota: Int,

    @Column(name = "AVA_COMENTARIO", length = 500, columnDefinition = "TEXT")
    var comentario: String? = null,

    @Column(name = "AVA_DATA", nullable = false)
    var data: Date,

    // Chaves Estrangeiras (Objetos)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USU_ID", referencedColumnName = "USU_ID", nullable = false)
    val usuario: Usuario,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAC_ID", referencedColumnName = "PAC_ID", nullable = false)
    val pacote: Pacote
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AVA_ID", nullable = false)
    var id = 0
}