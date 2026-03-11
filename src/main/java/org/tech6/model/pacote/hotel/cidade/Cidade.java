package org.tech6.model.pacote.hotel.cidade;

import org.tech6.model.pacote.hotel.cidade.estado.Estado;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "cid_cidade")
public class Cidade extends PanacheEntityBase {

    @Id
    @Column(name = "cid_id", nullable = false)
    public Long id;

    @Column(name = "cid_nome", length = 40, nullable = false)
    public String nome;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "est_id", nullable = false)
    public Estado estado;

    public Cidade() {
    }

    public Cidade(Long id, String nome, Estado estado) {
        this.id = id;
        this.nome = nome;
        this.estado = estado;
    }

}
