package com.destino.projeto_destino.model;

import com.destino.projeto_destino.model.transporteUtils.Meio;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tra_transporte")
public class Transporte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRA_ID", nullable = false)
    private int id;

    @Column(name = "TRA_EMPRESA", length = 100, nullable = false)
    private String empresa;

    @Column(name = "TRA_MEIO", nullable = false)
    private Meio meio;

    @Column(name = "TRA_PRECO", precision = 10, scale = 2, nullable = false)
    private BigDecimal preco;

    public Transporte() {
    }

    public Transporte(String empresa, Meio meio, BigDecimal preco) {
        this.empresa = empresa;
        this.meio = meio;
        this.preco = preco;
    }

    public int getId() {
        return id;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public Meio getMeio() {
        return meio;
    }

    public void setMeio(Meio meio) {
        this.meio = meio;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }
}
