package org.tech6.model.pacote.oferta;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.tech6.model.pacote.Pacote;
import org.tech6.model.pacote.hotel.Hotel;
import org.tech6.model.pacote.transporte.Transporte;
import org.tech6.util.model.pacote.OfertaStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "OFE_OFERTA")
public class Oferta extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "PAC_PRECO", precision = 10, scale = 2, nullable = false)
    public BigDecimal preco;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "PAC_DATA_INICIO_VIAGEM", nullable = false)
    public LocalDate inicio;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "PAC_DATA_FIM_VIAGEM", nullable = false)
    public LocalDate fim;

    @Column(name = "PAC_DISPONIBILIDADE", nullable = false)
    public int disponibilidade;

    @Column(name = "OFE_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    public OfertaStatus status;

    @ManyToOne
    @JoinColumn(name = "PAC_ID", referencedColumnName = "PAC_ID", nullable = false)
    @JsonIgnore
    public Pacote pacote;

    @ManyToOne
    @JoinColumn(name = "HOT_ID", referencedColumnName = "HOT_ID", nullable = false)
    public Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "TRA_ID", referencedColumnName = "TRA_ID", nullable = false)
    public Transporte transporte;

    public String getPrecoFormatado() {
        return String.format("%.2f", preco);
    }
}
