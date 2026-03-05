package org.tech6.model.pacote.hotel;

import org.tech6.dto.pacote.hotel.HotelRegistroDTO;
import org.tech6.model.pacote.hotel.cidade.Cidade;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "hot_hotel")
public class Hotel extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HOT_ID", nullable = false)
    public Long id;

    @Column(name = "HOT_NOME", length = 50, nullable = false)
    public String nome;

    @Column(name = "HOT_ENDERECO", length = 100, nullable = false)
    public String endereco;

    @Column(name = "HOT_diária", precision = 10, scale = 2, nullable = false)
    public int diaria;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "cid_id", nullable = false)
    public Cidade cidade;

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
