package com.destino.projeto_destino.model.pacote;

import com.destino.projeto_destino.model.pacote.hotel.Hotel;
import com.destino.projeto_destino.model.pacote.pacoteFoto.PacoteFoto;
import com.destino.projeto_destino.model.pacote.transporte.Transporte;
import com.destino.projeto_destino.model.usuario.Usuario;
import com.destino.projeto_destino.util.model.pacote.PacoteStatus;
import com.destino.projeto_destino.util.model.pacote.StringListConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

@Entity
@Table(name = "pac_pacote")
@Getter
@Setter
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

    @Column(name = "PAC_ITENS")
    @Convert(converter = StringListConverter.class)
    private ArrayList<String> tags;

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
    private PacoteStatus status;

    // Relacionamentos

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

    public Pacote() {
    }

    public Pacote(long id, String nome, String descricao, ArrayList<String> tags, BigDecimal preco, LocalDate inicio, LocalDate fim, int disponibilidade, PacoteStatus status, Transporte transporte, Hotel hotel, Usuario funcionario, PacoteFoto fotosDoPacote) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.tags = tags;
        this.preco = preco;
        this.inicio = inicio;
        this.fim = fim;
        this.disponibilidade = disponibilidade;
        this.status = status;
        this.transporte = transporte;
        this.hotel = hotel;
        this.funcionario = funcionario;
        this.fotosDoPacote = fotosDoPacote;
    }

    public String getPrecoFormatado() {
        return String.format("%.2f", preco);
    }
}