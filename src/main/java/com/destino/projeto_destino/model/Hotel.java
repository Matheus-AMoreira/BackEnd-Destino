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

    // Getters e Setters (omitidos para brevidade)
}
