package com.destino.projeto_destino.model.pacote.hotel;

import com.destino.projeto_destino.model.pacote.hotel.cidade.Cidade;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "hot_hotel")
@Getter
@Setter
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HOT_ID", nullable = false)
    private Integer id;

    @Column(name = "HOT_NOME", length = 50, nullable = false)
    private String nome;

    @Column(name = "HOT_ENDERECO", length = 100, nullable = false)
    private String endereco;

    @Column(name = "HOT_diária", precision = 10, scale = 2, nullable = false)
    private BigDecimal diaria;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cid_id", nullable = false)
    private Cidade cidade;

    public Hotel() {
    }

    public Hotel(String nome, String estado,Cidade cidade, String endereco, BigDecimal diaria) {
        this.nome = nome;
        this.cidade = cidade;
        this.endereco = endereco;
        this.diaria = diaria;
    }

}
