package com.destino.projeto_destino.model.pacote.pacoteFoto;


import com.destino.projeto_destino.model.pacote.Pacote;
import com.destino.projeto_destino.model.pacote.pacoteFoto.foto.Foto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "pcf_foto", length = 255, nullable = false)
    private String fotoDoPacote;

    // Relacionamentos

    @JsonIgnore
    @OneToMany(mappedBy = "fotosDoPacote")
    private Set<Pacote> pacotes;

    @OneToMany(mappedBy = "pacoteFoto", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Foto> fotos;

    public PacoteFoto() {
    }

    public PacoteFoto(String nome, Set<Pacote> pacotes, Set<Foto> fotos) {
        this.nome = nome;
        this.pacotes = pacotes;
        this.fotos = fotos;
    }
}
