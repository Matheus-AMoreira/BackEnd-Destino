package com.fatec.destino.model.pacote.tag;

import com.fatec.destino.model.pacote.Pacote;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "TAG_TAGs")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TAG_ID", nullable = false)
    private long id;

    @Column(name = "TAG_NOME", nullable = false)
    private String nome;

    // Relações

    @ManyToMany(mappedBy = "tags")
    private Set<Pacote> pacotes;

}
