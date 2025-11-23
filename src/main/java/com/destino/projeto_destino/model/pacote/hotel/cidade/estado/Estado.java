package com.destino.projeto_destino.model.pacote.hotel.cidade.estado;

import com.destino.projeto_destino.model.pacote.hotel.cidade.estado.regiao.Regiao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "est_estado")
@Getter
@Setter
public class Estado {

    @Id
    @Column(name = "est_id", nullable = false)
    private Long id;

    @Column(name = "est_sigla", length = 2, nullable = false)
    private String sigla;

    @Column(name = "est_nome", length = 100, nullable = false)
    private String nome;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "reg_id")
    private Regiao regiao;

    public Estado() {
    }

    public Estado(Long id, String sigla, String nome, Regiao regiao) {
        this.id = id;
        this.sigla = sigla;
        this.nome = nome;
        this.regiao = regiao;
    }
}
