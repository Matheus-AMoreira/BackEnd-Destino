package com.destino.projeto_destino.model.pacote.pacoteFoto.foto;

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
@Table(name = "fot_foto")
public class Foto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FOT_ID", nullable = false)
    private int id;

    @Column(name = "FOT_NOME", length = 100, nullable = false)
    private String nome;

    @Column(name = "FOT_URL", length = 255, nullable = false)
    private String url;

    // Removido: private PacoteFoto pacoteFoto;
    // Motivo: Simplificação para relação unidirecional.

    public Foto() {
    }

    public Foto(String url, String nome) {
        this.url = url;
        this.nome = nome;
    }
}
