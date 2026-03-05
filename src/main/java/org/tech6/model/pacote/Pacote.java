package org.tech6.model.pacote;

import org.tech6.model.pacote.hotel.Hotel;
import org.tech6.model.pacote.oferta.Oferta;
import org.tech6.model.pacote.pacoteFoto.PacoteFoto;
import org.tech6.model.pacote.tag.Tag;
import org.tech6.model.pacote.transporte.Transporte;
import org.tech6.model.usuario.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "pac_pacote")
public class Pacote extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAC_ID", nullable = false)
    public long id;

    @Column(name = "PAC_NOME", length = 100, nullable = false)
    public String nome;

    @Column(name = "PAC_DESCRICAO", nullable = false, columnDefinition = "TEXT")
    public String descricao;

    // Relacionamentos

    @ManyToMany
    @JoinTable(name = "PAC_PACOTE_TAGS", joinColumns = @JoinColumn(name = "PAC_ID"), inverseJoinColumns = @JoinColumn(name = "TAG_ID"))
    public Set<Tag> tags;

    @OneToMany(mappedBy = "pacote", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Oferta> ofertas = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "TRA_ID", referencedColumnName = "TRA_ID", nullable = false)
    public Transporte transporte;

    @ManyToOne
    @JoinColumn(name = "HOT_ID", referencedColumnName = "HOT_ID", nullable = false)
    public Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "USU_ID", referencedColumnName = "USU_ID", nullable = false)
    public Usuario funcionario;

    @ManyToOne
    @JoinColumn(name = "pcf_id", referencedColumnName = "pcf_id", nullable = true)
    public PacoteFoto fotosDoPacote;

    public Pacote() {
    }
}