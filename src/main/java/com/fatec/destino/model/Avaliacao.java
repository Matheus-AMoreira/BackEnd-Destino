package com.fatec.destino.model;

import com.fatec.destino.model.pacote.Pacote;
import com.fatec.destino.model.usuario.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "ava_avaliacao")
@Setter
@Getter
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AVA_ID", nullable = false)
    private int id;

    @Column(name = "AVA_NOTA", nullable = false)
    private int nota;

    @Column(name = "AVA_COMENTARIO", length = 500, columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "AVA_DATA", nullable = false)
    private Date data;

    // Chaves Estrangeiras (Objetos)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USU_ID", referencedColumnName = "USU_ID", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAC_ID", referencedColumnName = "PAC_ID", nullable = false)
    private Pacote pacote;
}
