package com.destino.projeto_destino.model.local;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "est_estado")
@Getter
@Setter
public class Estado {

    @Id
    @Column(name = "est_id", nullable = false)
    private Long id;

    @Column(name = "est_sigla", length = 2,nullable = false)
    private String sigla;

    @Column(name = "est_nome", length = 100,nullable = false)
    private String nome;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "reg_id")
    private Regiao regiao;

    @OneToMany(mappedBy = "estado")
    private List<Cidade> cidades;

    public Estado() {
    }

    public Estado(Long id, String sigla, String nome, Regiao regiao) {
        this.id = id;
        this.sigla = sigla;
        this.nome = nome;
        this.regiao = regiao;
    }
}
