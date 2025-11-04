package com.destino.projeto_destino.model;

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

    @Column(name = "PAC_DESCRICAO", length = 255, nullable = false)
    private String descricao;

    @Column(name = "PAC_PRECO", precision = 10, scale = 2, nullable = false)
    private BigDecimal preco;

    @Column(name = "PAC_DATA_INICIO_VIAGEM", nullable = false)
    private Date data;

    @Column(name = "PAC_DISPONIBILIDADE", nullable = false)
    private int disponibilidade;

    @Column(name = "PAC_STATUS", length = 50, nullable = false)
    private String status;

    // Relacionamento N:M com Foto. TODAS as fotos são gerenciadas por aqui.
    @OneToMany(mappedBy = "pacote", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PacoteFoto> fotosDoPacote;

    // Chaves Estrangeiras (Objetos)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAC_ID_ADMIN", referencedColumnName = "USU_ID", nullable = false)
    private Usuario admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAC_ID_VIAGEM", referencedColumnName = "VIA_ID", nullable = false)
    private Viagem viagem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAC_ID_HOTEL", referencedColumnName = "HOT_ID", nullable = false)
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAC_ID_TRANSPORTE", referencedColumnName = "TRA_ID", nullable = false)
    private Transporte transporte;

    // Getters e Setters (omitidos para brevidade)
}
