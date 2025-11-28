package com.destino.projeto_destino.model.pacote.pacoteFoto;

import com.destino.projeto_destino.model.pacote.pacoteFoto.foto.Foto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "pcf_pacote_foto")
public class PacoteFoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PCF_ID", nullable = false)
    private int id;

    @Column(name = "PCF_NOME", nullable = false)
    private String nome;

    @Column(name = "PCF_foto", length = 255, nullable = false)
    private String fotoDoPacote;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "PCF_ID")
    private Set<Foto> fotos = new HashSet<>();

    public PacoteFoto() {
    }

    public PacoteFoto(String nome, String fotoDoPacote, Set<Foto> fotos) {
        this.nome = nome;
        this.fotoDoPacote = fotoDoPacote;
        this.fotos = fotos;
    }
}
