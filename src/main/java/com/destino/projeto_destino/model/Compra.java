package com.destino.projeto_destino.model;

import com.destino.projeto_destino.model.compraUtils.Status;
import jakarta.persistence.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "com_compra")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "COM_ID", nullable = false)
    private UUID id;

    @Column(name = "COM_DATA_COMPRA", nullable = false)
    private Date dataCompra; // Ajustado para melhor clareza

    @Enumerated(EnumType.STRING)
    @Column(name = "COM_STATUS", nullable = false, length = 50)
    private Status status; // Ajustado para melhor clareza

    // Chaves Estrangeiras (Objetos)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COM_ID_USUARIO", referencedColumnName = "USU_ID", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COM_ID_PACOTE", referencedColumnName = "PAC_ID", nullable = false)
    private Pacote pacote;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COM_ID_PAGAMENTO", referencedColumnName = "PAG_ID", nullable = false)
    private Pagamento pagamento;

    // Construtores, Getters e Setters (Omitidos para brevidade)
}
