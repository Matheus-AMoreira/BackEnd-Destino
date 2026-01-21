package com.fatec.destino.model.pacote.pacoteFoto

import com.fatec.destino.model.pacote.pacoteFoto.foto.Foto
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "pcf_pacote_foto")
class PacoteFoto (
    @Column(name = "PCF_NOME", nullable = false)
    var nome: String? = null,

    @Column(name = "PCF_foto", columnDefinition = "TEXT", nullable = false)
    var fotoDoPacote: String? = null,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "PCF_ID")
    var fotos: MutableSet<Foto?>? = HashSet<Foto?>()
){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PCF_ID", nullable = false)
    var id: Long? = null
}