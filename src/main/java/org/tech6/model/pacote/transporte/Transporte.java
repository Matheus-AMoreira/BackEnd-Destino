package org.tech6.model.pacote.transporte;

import org.tech6.util.model.transporte.Meio;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tra_transporte")
public class Transporte extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRA_ID", nullable = false)
    public Long id;

    @Column(name = "TRA_EMPRESA", length = 100, nullable = false)
    public String empresa;

    @Column(name = "TRA_MEIO", nullable = false)
    public Meio meio;

    @Column(name = "TRA_PRECO", precision = 10, scale = 2, nullable = false)
    public int preco;

    public Transporte() {
    }

    public Transporte(String empresa, Meio meio, int preco) {
        this.empresa = empresa;
        this.meio = meio;
        this.preco = preco;
    }
}
