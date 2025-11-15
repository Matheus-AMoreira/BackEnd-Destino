package com.destino.projeto_destino.model.pacote.transporte;

import com.destino.projeto_destino.util.transporte.Meio;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
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
}
