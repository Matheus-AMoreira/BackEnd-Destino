package com.destino.projeto_destino.model;

import com.destino.projeto_destino.model.pagamentoUtils.Meio;
import com.destino.projeto_destino.model.pagamentoUtils.Metodo;
import com.destino.projeto_destino.model.pagamentoUtils.Processamento;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "pag_pagamento")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAG_ID", nullable = false)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "PAG_METODO", nullable = false, length = 50)
    private Metodo metodo;

    @Enumerated(EnumType.STRING)
    @Column(name = "PAG_MEIO", nullable = false, length = 50)
    private Meio meio;

    @Enumerated(EnumType.STRING)
    @Column(name = "PAG_PROCESSAMENTO", nullable = false, length = 50)
    private Processamento processamento;

    @Column(name = "PAG_DESCONTO", precision = 10, scale = 2)
    private BigDecimal desconto;

    @Column(name = "PAG_VALOR_BRUTO", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor; // Alterado para valor bruto para clareza

    @Column(name = "PAG_VALOR_FINAL", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorFinal;

    // Construtores, Getters e Setters (Omitidos para brevidade)
}
