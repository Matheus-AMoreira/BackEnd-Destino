package com.fatec.destino.model.pacote.oferta;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fatec.destino.model.pacote.Pacote;
import com.fatec.destino.util.model.pacote.OfertaStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "OFE_OFERTA")
public class Oferta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "PAC_PRECO", precision = 10, scale = 2, nullable = false)
    private BigDecimal preco;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "PAC_DATA_INICIO_VIAGEM", nullable = false)
    private LocalDate inicio;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "PAC_DATA_FIM_VIAGEM", nullable = false)
    private LocalDate fim;

    @Column(name = "PAC_DISPONIBILIDADE", nullable = false)
    private int disponibilidade;

    @Column(name = "PAC_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private OfertaStatus status;

    // Relações

    @ManyToOne
    @JoinColumn(name = "PAC_ID")
    private Pacote pacote;

    public String getPrecoFormatado() {
        return String.format("%.2f", preco);
    }
}
