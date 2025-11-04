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

    @Column(name = "HOT_NOME", length = 150, nullable = false)
    private String nome;

    @Column(name = "HOT_LOCAL", length = 200, nullable = false)
    private String local;

    // Getters e Setters (omitidos para brevidade)
}
