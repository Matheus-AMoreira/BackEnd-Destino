package com.destino.projeto_destino.model;

import com.destino.projeto_destino.model.pacote.Pacote;
import com.destino.projeto_destino.model.usuario.Usuario;
import com.destino.projeto_destino.util.model.compra.Metodo;
import com.destino.projeto_destino.util.model.compra.Processador;
import com.destino.projeto_destino.util.model.compra.StatusCompra;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "com_compra")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COM_ID", nullable = false)
    private Long id;

    @JsonFormat(pattern = "dd-MM-yyyy")
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

    @Column(name = "COM_PARCELAS", nullable = false)
    private int parcelas;

    @Column(name = "COM_VALOR_FINAL", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorFinal;

    // Chaves Estrangeiras

    @ManyToOne
    @JoinColumn(name = "USU_ID", referencedColumnName = "USU_ID", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "PAC_ID", referencedColumnName = "PAC_ID", nullable = false)
    private Pacote pacote;

}
