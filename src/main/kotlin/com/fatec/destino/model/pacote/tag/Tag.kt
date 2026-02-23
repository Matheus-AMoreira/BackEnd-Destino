package com.fatec.destino.model.pacote.tag

import com.fatec.destino.model.pacote.Pacote
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

@Entity
@Table(name = "tag_tag")
class Tag(
    @Column(name = "TAG_NOME", length = 50, nullable = false, unique = true)
    var nome: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TAG_ID", nullable = false)
    var id: Long? = null

    @ManyToMany(mappedBy = "tags")
    var pacotes: Set<Pacote> = emptySet()
}
