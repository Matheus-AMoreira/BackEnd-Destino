package com.destino.projeto_destino.model;

import com.destino.projeto_destino.model.pacoteUtils.Transporte;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "pac_pacote")
public class Pacote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAC_ID", nullable = false)
    private int id;

    @Column(name = "PAC_NOME", length = 100, nullable = false)
    private String nome;

    @Column(name = "PAC_LOCAL", length = 100, nullable = false)
    private String local;

    @Column(name = "PAC_DESCRICAO", nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "PAC_PRECO", precision = 10, scale = 2, nullable = false)
    private BigDecimal preco;

    @Column(name = "PAC_DATA_INICIO_VIAGEM", nullable = false)
    private Date data;

    @Column(name = "PAC_DISPONIBILIDADE", nullable = false)
    private int disponibilidade;

    @Column(name = "PAC_STATUS", length = 50, nullable = false)
    private String status;

    @Column(name = "PAC_TRANSPORTE",nullable = false)
    @Enumerated(EnumType.STRING)
    private Transporte transporte;

    // Relacionamento N:M com Foto. TODAS as fotos são gerenciadas por aqui.
    @OneToMany(mappedBy = "pacote", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PacoteFoto> fotosDoPacote;

    // Chaves Estrangeiras (Objetos)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USU_ID", referencedColumnName = "USU_ID", nullable = false)
    private Usuario admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HOT_ID", referencedColumnName = "HOT_ID", nullable = false)
    private Hotel hotel;

    // Getters e Setters (omitidos para brevidade)
}
