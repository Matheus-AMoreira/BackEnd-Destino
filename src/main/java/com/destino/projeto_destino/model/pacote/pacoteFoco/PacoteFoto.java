package com.destino.projeto_destino.model.pacote.pacoteFoco;

import com.destino.projeto_destino.model.pacote.Pacote;
import com.destino.projeto_destino.model.pacote.pacoteFoco.foto.Foto;
import jakarta.persistence.*;
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

    // Relacionamentos

    @OneToMany(mappedBy = "fotosDoPacote", fetch = FetchType.LAZY)
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
