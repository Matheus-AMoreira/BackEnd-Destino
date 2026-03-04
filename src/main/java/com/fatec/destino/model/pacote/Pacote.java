package com.fatec.destino.model.pacote;

import com.fatec.destino.model.pacote.hotel.Hotel;
import com.fatec.destino.model.pacote.oferta.Oferta;
import com.fatec.destino.model.pacote.pacoteFoto.PacoteFoto;
import com.fatec.destino.model.pacote.tag.Tag;
import com.fatec.destino.model.pacote.transporte.Transporte;
import com.fatec.destino.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "pac_pacote")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pacote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAC_ID", nullable = false)
    private long id;

    @Column(name = "PAC_NOME", length = 100, nullable = false)
    private String nome;

    @Column(name = "PAC_DESCRICAO", nullable = false, columnDefinition = "TEXT")
    private String descricao;

    // Relacionamentos

    @ManyToMany
    @JoinTable(
            name = "PAC_PACOTE_TAGS",
            joinColumns = @JoinColumn(name = "PAC_ID"),
            inverseJoinColumns = @JoinColumn(name = "TAG_ID")
    )
    private Set<Tag> tags;

    @OneToMany(mappedBy = "pacote", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Oferta> ofertas = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "TRA_ID", referencedColumnName = "TRA_ID", nullable = false)
    private Transporte transporte;

    @ManyToOne
    @JoinColumn(name = "HOT_ID", referencedColumnName = "HOT_ID", nullable = false)
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "USU_ID", referencedColumnName = "USU_ID", nullable = false)
    private Usuario funcionario;

    @ManyToOne
    @JoinColumn(name = "pcf_id", referencedColumnName = "pcf_id", nullable = true)
    private PacoteFoto fotosDoPacote;

}