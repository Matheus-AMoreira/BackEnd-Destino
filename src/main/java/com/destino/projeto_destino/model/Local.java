package com.destino.projeto_destino.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "loc_local")
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOC_ID")
    private int id;
    private String nome;
    private List<String> itens;

    public Local(String nome, List<String> itens) {
        this.nome = nome;
        this.itens = itens;
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
}
