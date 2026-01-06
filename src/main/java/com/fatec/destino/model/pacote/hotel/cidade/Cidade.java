package com.fatec.destino.model.pacote.hotel.cidade;

import com.fatec.destino.model.pacote.hotel.cidade.estado.Estado;
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
@Table(name = "cid_cidade")
@Getter
@Setter
public class Cidade {

    @Id
    @Column(name = "cid_id", nullable = false)
    private Long id;

    @Column(name = "cid_nome", length = 40, nullable = false)
    private String nome;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "est_id", nullable = false)
    private Estado estado;

    public Cidade() {
    }

    public Cidade(Long id, String nome, Estado estado) {
        this.id = id;
        this.nome = nome;
        this.estado = estado;
    }

}
