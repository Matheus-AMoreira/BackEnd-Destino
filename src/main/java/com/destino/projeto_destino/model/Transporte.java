package com.destino.projeto_destino.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tra_transporte")
public class Transporte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRA_ID", nullable = false)
    private int id;

    @Column(name = "TRA_NOME", length = 100, nullable = false)
    private String nome;

    @Column(name = "TRA_PRECO", precision = 10, scale = 2, nullable = false)
    private BigDecimal preco;
}
