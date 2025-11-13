package com.destino.projeto_destino.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pcf_pacote_foto")
public class PacoteFoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PCF_ID", nullable = false)
    private int id;

    @Column(name = "PCF_NOME", nullable = false)
    private String nome;

    // Relacionamentos

    @ManyToOne
    @JoinColumn(name = "PAC_ID", referencedColumnName = "PAC_ID", nullable = false)
    private Pacote pacote;

    @ManyToOne
    @JoinColumn(name = "FOT_ID", referencedColumnName = "FOT_ID", nullable = false)
    private Foto foto;

}
