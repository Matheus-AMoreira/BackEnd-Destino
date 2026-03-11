package org.tech6.model.pacote.hotel.cidade.estado.regiao;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reg_regiao")
public class Regiao extends PanacheEntityBase {

    @Id
    @Column(name = "reg_id", nullable = false)
    public Long id;

    @Column(name = "reg_sigla", length = 2, nullable = false)
    public String sigla;

    @Column(name = "reg_nome", length = 12, nullable = false)
    public String nome;

    public Regiao() {
    }

    public Regiao(Long id, String sigla, String nome) {
        this.id = id;
        this.sigla = sigla;
        this.nome = nome;
    }

}
