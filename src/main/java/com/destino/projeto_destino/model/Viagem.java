package com.destino.projeto_destino.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "via_viagem")
public class Viagem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private List<String> itens;
    private double valor;

    public Viagem(String nome, List<String> itens, double valor) {
        this.nome = nome;
        this.itens = itens;
        this.valor = valor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<String> getItens() {
        return itens;
    }

    public void setItens(List<String> itens) {
        this.itens = itens;
    }

    public String getValor() {
        return String.format("%.2f", valor);
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
