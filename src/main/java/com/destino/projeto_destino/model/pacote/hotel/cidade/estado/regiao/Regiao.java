package com.destino.projeto_destino.model.pacote.hotel.cidade.estado.regiao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reg_regiao")
@Getter
@Setter
public class Regiao {

    @Id
    @Column(name = "reg_id", nullable = false)
    private Long id;

    @Column(name = "reg_sigla", length = 2, nullable = false)
    private String sigla;

    @Column(name = "reg_nome", length = 12, nullable = false)
    private String nome;

    public Regiao() {
    }

    public Regiao(Long id, String sigla, String nome) {
        this.id = id;
        this.sigla = sigla;
        this.nome = nome;
    }


}
