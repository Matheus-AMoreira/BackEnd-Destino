package com.destino.projeto_destino.model;

import jakarta.persistence.*;


@Entity
@Table(name = "via_viagem")
public class Viagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VIA_ID", nullable = false)
    private int id;

    @Column(name = "VIA_LOCAL", length = 200, nullable = false)
    private String local;

    // Getters e Setters (omitidos para brevidade)
}
