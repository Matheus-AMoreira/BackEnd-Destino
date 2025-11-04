package com.destino.projeto_destino.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pcf_pacote_foto")
public class PacoteFoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PCF_ID", nullable = false)
    private int id;

    // Relacionamentos

    @ManyToOne
    @JoinColumn(name = "PCF_ID_PACOTE", referencedColumnName = "PAC_ID", nullable = false)
    private Pacote pacote;

    @ManyToOne
    @JoinColumn(name = "PCF_ID_FOTO", referencedColumnName = "FOT_ID", nullable = false)
    private Foto foto;

    // Getters e Setters (omitidos para brevidade)
}
