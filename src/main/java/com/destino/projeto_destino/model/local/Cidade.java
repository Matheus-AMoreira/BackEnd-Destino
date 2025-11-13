package com.destino.projeto_destino.model.local;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cid_cidade")
@Getter
@Setter
public class Cidade {

    @Id
    @Column(name = "cid_id",nullable = false)
    private Long id;

    @Column(name = "cid_nome", length = 40,nullable = false)
    private String nome;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "est_id", nullable = false)
    private Estado estado;

    public Cidade() {}

    public Cidade(Long id, String nome, Estado estado) {
        this.id = id;
        this.nome = nome;
        this.estado = estado;
    }

}
