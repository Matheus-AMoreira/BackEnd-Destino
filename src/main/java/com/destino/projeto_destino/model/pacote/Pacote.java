package com.destino.projeto_destino.model.pacote;

import com.destino.projeto_destino.model.pacote.hotel.Hotel;
import com.destino.projeto_destino.model.pacote.pacoteFoco.PacoteFoto;
import com.destino.projeto_destino.model.pacote.transporte.Transporte;
import com.destino.projeto_destino.model.usuario.Usuario;
import com.destino.projeto_destino.util.pacote.Status;
import com.destino.projeto_destino.util.pacote.StringListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "pac_pacote")
@Getter
@Setter
public class Pacote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAC_ID", nullable = false)
    private int id;

    @Column(name = "PAC_NOME", length = 100, nullable = false)
    private String nome;

    @Column(name = "PAC_DESCRICAO", nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "PAC_ITENS")
    @Convert(converter = StringListConverter.class)
    private List<String> tags;

    @Column(name = "PAC_PRECO", precision = 10, scale = 2, nullable = false)
    private BigDecimal preco;

    @Column(name = "PAC_DATA_INICIO_VIAGEM", nullable = false)
    private Date inicio;

    @Column(name = "PAC_DATA_FIM_VIAGEM", nullable = false)
    private Date fim;

    @Column(name = "PAC_DISPONIBILIDADE", nullable = false)
    private int disponibilidade;

    @Column(name = "PAC_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    // Relacionamentos

    @ManyToOne
    @JoinColumn(name = "TRA_ID", referencedColumnName = "TRA_ID", nullable = false)
    private Transporte transporte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HOT_ID", referencedColumnName = "HOT_ID", nullable = false)
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USU_ID", referencedColumnName = "USU_ID", nullable = false)
    private Usuario funcionarion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pcf_id")
    private PacoteFoto fotosDoPacote;

    public Pacote() {
    }

    public Pacote(String nome, String descricao, List<String> tags, BigDecimal preco, Date inicio, Date fim, int disponibilidade, Status status, Transporte transporte, PacoteFoto fotosDoPacote, Usuario funcionarion, Hotel hotel) {
        this.nome = nome;
        this.descricao = descricao;
        this.tags = tags;
        this.preco = preco;
        this.inicio = inicio;
        this.fim = fim;
        this.disponibilidade = disponibilidade;
        this.status = status;
        this.transporte = transporte;
        this.fotosDoPacote = fotosDoPacote;
        this.funcionarion = funcionarion;
        this.hotel = hotel;
    }

    public String getPreco() {
        return String.format("%.2f", preco);
    }
}
