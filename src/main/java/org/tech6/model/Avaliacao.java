package org.tech6.model;

import org.tech6.model.pacote.Pacote;
import org.tech6.model.usuario.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Date;

@Entity
@Table(name = "ava_avaliacao")
public class Avaliacao extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AVA_ID", nullable = false)
    public int id;

    @Column(name = "AVA_NOTA", nullable = false)
    public int nota;

    @Column(name = "AVA_COMENTARIO", length = 500, columnDefinition = "TEXT")
    public String comentario;

    @Column(name = "AVA_DATA", nullable = false)
    public Date data;

    // Chaves Estrangeiras (Objetos)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USU_ID", referencedColumnName = "USU_ID", nullable = false)
    public Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAC_ID", referencedColumnName = "PAC_ID", nullable = false)
    public Pacote pacote;

    public Avaliacao() {
    }
}
