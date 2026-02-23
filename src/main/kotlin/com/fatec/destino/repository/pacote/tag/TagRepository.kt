package com.fatec.destino.repository.pacote.tag

import com.fatec.destino.model.pacote.tag.Tag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TagRepository : JpaRepository<Tag, Long> {
    fun findByNome(nome: String): Tag?
    fun findByNomeIn(nomes: List<String>): Set<Tag>
}
