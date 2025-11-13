package com.destino.projeto_destino.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "hot_hotel")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HOT_ID", nullable = false)
    private int id;

    @Column(name = "HOT_NOME", length = 50, nullable = false)
    private String nome;

    @Column(name = "HOT_CIDADE", length = 100, nullable = false)
    private String cidade;

    @Column(name = "HOT_ENDERECO", length = 100, nullable = false)
    private String endereco;

    @Column(name = "HOT_diária", length = 100, nullable = false)
    private double diaria;

    public Hotel(String nome, String cidade, String endereco, double diaria) {
        this.nome = nome;
        this.cidade = cidade;
        this.endereco = endereco;
        this.diaria = diaria;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getdiaria() {
        return String.format("%.2f", diaria);
    }

    public void setPrecoDia(double diaria) {
        this.diaria = diaria;
    }
}
