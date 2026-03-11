package org.tech6.model.pacote.hotel.cidade.estado;

import org.tech6.model.pacote.hotel.cidade.estado.regiao.Regiao;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "est_estado")
public class Estado extends PanacheEntityBase {

    @Id
    @Column(name = "est_id", nullable = false)
    public Long id;

    @Column(name = "est_sigla", length = 2, nullable = false)
    public String sigla;

    @Column(name = "est_nome", length = 100, nullable = false)
    public String nome;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "reg_id")
    public Regiao regiao;

    public Estado() {
    }

    public Estado(Long id, String sigla, String nome, Regiao regiao) {
        this.id = id;
        this.sigla = sigla;
        this.nome = nome;
        this.regiao = regiao;
    }
}
