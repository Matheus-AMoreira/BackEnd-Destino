package org.tech6.model.pacote.pacoteFoto;

import org.tech6.model.pacote.pacoteFoto.foto.Foto;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pcf_pacote_foto")
public class PacoteFoto extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PCF_ID", nullable = false)
    public Long id;

    @Column(name = "PCF_NOME", nullable = false)
    public String nome;

    @Column(name = "PCF_foto", columnDefinition = "TEXT", nullable = false)
    public String fotoDoPacote;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "PCF_ID")
    public Set<Foto> fotos = new HashSet<>();

    public PacoteFoto() {
    }

    public PacoteFoto(String nome, String fotoDoPacote, Set<Foto> fotos) {
        this.nome = nome;
        this.fotoDoPacote = fotoDoPacote;
        this.fotos = fotos;
    }
}
