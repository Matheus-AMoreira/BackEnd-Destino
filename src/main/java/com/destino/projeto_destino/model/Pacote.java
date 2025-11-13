package com.destino.projeto_destino.model;

import com.destino.projeto_destino.model.pacoteUtils.Status;
import com.destino.projeto_destino.model.pacoteUtils.StringListConverter;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "pac_pacote")
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

    // Chaves Estrangeiras (Objetos)

    @ManyToOne
    @JoinColumn(name = "TRA_ID", referencedColumnName = "TRA_ID", nullable = false)
    private Transporte transporte;

    @OneToMany(mappedBy = "pacote", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PacoteFoto> fotosDoPacote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USU_ID", referencedColumnName = "USU_ID", nullable = false)
    private Usuario funcionarion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HOT_ID", referencedColumnName = "HOT_ID", nullable = false)
    private Hotel hotel;

    public Pacote() {
    }

    public Pacote(String nome, String descricao, List<String> tags, BigDecimal preco, Date inicio, Date fim, int disponibilidade, Status status, Transporte transporte, Set<PacoteFoto> fotosDoPacote, Usuario funcionarion, Hotel hotel) {
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    // Exemplo de getter (não precisa de lógica de conversão aqui)
    public List<String> getTags() {
        return this.tags;
    }

    public String getPreco() {
        return String.format("%.2f", preco);
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public int getDisponibilidade() {
        return disponibilidade;
    }

    public void setDisponibilidade(int disponibilidade) {
        this.disponibilidade = disponibilidade;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Transporte getTransporte() {
        return transporte;
    }

    public void setTransporte(Transporte transporte) {
        this.transporte = transporte;
    }

    public Set<PacoteFoto> getFotosDoPacote() {
        return fotosDoPacote;
    }

    public void setFotosDoPacote(Set<PacoteFoto> fotosDoPacote) {
        this.fotosDoPacote = fotosDoPacote;
    }

    public Usuario getAdmin() {
        return funcionarion;
    }

    public void setAdmin(Usuario funcionarion) {
        this.funcionarion = funcionarion;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }
}
