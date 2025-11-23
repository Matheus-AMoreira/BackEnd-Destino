package com.destino.projeto_destino.model.pacote.hotel;

import com.destino.projeto_destino.dto.pacote.hotel.HotelRegistroDTO;
import com.destino.projeto_destino.model.pacote.hotel.cidade.Cidade;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "hot_hotel")
@Getter
@Setter
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HOT_ID", nullable = false)
    private int id;

    @Column(name = "HOT_NOME", length = 50, nullable = false)
    private String nome;

    @Column(name = "HOT_ENDERECO", length = 100, nullable = false)
    private String endereco;

    @Column(name = "HOT_diária", precision = 10, scale = 2, nullable = false)
    private int diaria;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "cid_id", nullable = false)
    private Cidade cidade;

    public Hotel() {
    }

    public Hotel(String nome, String endereco, int diaria, Cidade cidade) {
        this.nome = nome;
        this.endereco = endereco;
        this.diaria = diaria;
        this.cidade = cidade;
    }

    public Hotel(HotelRegistroDTO hotel, Cidade cidade) {
        this.nome = hotel.nome();
        this.endereco = hotel.endereco();
        this.diaria = hotel.diaria();
        this.cidade = cidade;
    }
}
