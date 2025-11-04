package com.destino.projeto_destino.model;

import jakarta.persistence.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "ava_avaliacao")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AVA_ID", nullable = false)
    private int id;

    @Column(name = "AVA_NOTA", nullable = false)
    private int nota;

    @Column(name = "AVA_COMENTARIO", length = 500)
    private String comentario;

    @Column(name = "AVA_DATA", nullable = false)
    private Date data;

    // Chaves Estrangeiras (Objetos)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AVA_ID_USUARIO", referencedColumnName = "USU_ID", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AVA_ID_PACOTE", referencedColumnName = "PAC_ID", nullable = false)
    private Pacote pacote;

    // Construtores, Getters e Setters (Omitidos para brevidade)
}
