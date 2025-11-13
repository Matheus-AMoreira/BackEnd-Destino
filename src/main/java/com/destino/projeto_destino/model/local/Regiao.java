package com.destino.projeto_destino.model.local;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "reg_regiao")
@Getter
@Setter
public class Regiao {

    @Id
    @Column(name = "reg_id", nullable = false)
    private Long id;

    @Column(name = "reg_sigla", length = 2,nullable = false)
    private String sigla;

    @Column(name = "reg_nome", length = 12,nullable = false)
    private String nome;

    @OneToMany(mappedBy = "regiao")
    private List<Estado> estados;

    public Regiao() {}

    public Regiao(Long id, String sigla, String nome) {
        this.id = id;
        this.sigla = sigla;
        this.nome = nome;
    }


}
