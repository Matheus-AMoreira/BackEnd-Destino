package com.destino.projeto_destino.model.pacote.transporte;

import com.destino.projeto_destino.util.model.transporte.Meio;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

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
    private int preco;

    public Transporte() {
    }

    public Transporte(String empresa, Meio meio, int preco) {
        this.empresa = empresa;
        this.meio = meio;
        this.preco = preco;
    }
}
