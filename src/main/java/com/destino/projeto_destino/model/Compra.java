package com.destino.projeto_destino.model;

import com.destino.projeto_destino.model.compraUtils.StatusCompra;
import com.destino.projeto_destino.model.compraUtils.Metodo;
import com.destino.projeto_destino.model.compraUtils.Processador;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "com_compra")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COM_ID", nullable = false)
    private Long id;

    @Column(name = "COM_DATA_COMPRA", nullable = false)
    private Date dataCompra;

    @Enumerated(EnumType.STRING)
    @Column(name = "COM_STATUS", nullable = false, length = 50)
    private StatusCompra status;

    @Enumerated(EnumType.STRING)
    @Column(name = "COM_METODO", nullable = false, length = 50)
    private Metodo metodo;

    @Enumerated(EnumType.STRING)
    @Column(name = "COM_PROCESSADOR", nullable = false, length = 50)
    private Processador processadorPagamento;

    @Column(name = "COM_VALOR_BRUTO", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorBruto;

    @Column(name = "COM_DESCONTO", precision = 10, scale = 2)
    private BigDecimal desconto;

    @Column(name = "COM_VALOR_FINAL", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorFinal;

    // Chaves Estrangeiras

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USU_ID", referencedColumnName = "USU_ID", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAC_ID", referencedColumnName = "PAC_ID", nullable = false)
    private Pacote pacote;

}
