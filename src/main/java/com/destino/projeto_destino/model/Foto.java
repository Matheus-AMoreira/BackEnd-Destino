package com.destino.projeto_destino.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "fot_foto")
public class Foto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FOT_ID", nullable = false)
    private int id;

    @Column(name = "FOT_NOME", length = 100, nullable = false)
    private String nome;

    @Column(name = "FOT_URL", length = 255, nullable = false)
    private String url;

    // Getters e Setters (omitidos para brevidade)
}
