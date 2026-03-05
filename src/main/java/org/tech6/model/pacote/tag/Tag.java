package org.tech6.model.pacote.tag;

import org.tech6.model.pacote.Pacote;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "TAG_TAGs")
public class Tag extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TAG_ID", nullable = false)
    public long id;

    @Column(name = "TAG_NOME", nullable = false)
    public String nome;

    @ManyToMany(mappedBy = "tags")
    public Set<Pacote> pacotes;

    public Tag() {
    }

    public Tag(String nome) {
        this.nome = nome;
    }
}
