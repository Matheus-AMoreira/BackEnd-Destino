package org.tech6.model.pacote.pacoteFoto.foto;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fot_foto")
public class Foto extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FOT_ID", nullable = false)
    public Long id;

    @Column(name = "FOT_NOME", length = 100, nullable = false)
    public String nome;

    @Column(name = "FOT_URL", nullable = false)
    public String url;

    public Foto() {
    }

    public Foto(String url, String nome) {
        this.url = url;
        this.nome = nome;
    }
}
