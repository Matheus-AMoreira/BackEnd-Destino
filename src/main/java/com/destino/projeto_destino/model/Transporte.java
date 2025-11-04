package com.destino.projeto_destino.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tra_transporte")
public class Transporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRA_ID", nullable = false)
    private int id;

    @Column(name = "TRA_COMPANHIA", length = 100, nullable = false)
    private String companhia;

    @Column(name = "TRA_METODO", length = 50, nullable = false)
    private String metodo;

    // Getters e Setters (omitidos para brevidade)
}
