package org.tech6.model;

import org.tech6.model.pacote.Pacote;
import org.tech6.model.pacote.oferta.Oferta;
import org.tech6.model.usuario.Usuario;
import org.tech6.util.model.compra.Metodo;
import org.tech6.util.model.compra.Processador;
import org.tech6.util.model.compra.StatusCompra;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "com_compra")
public class Compra extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "COM_ID", nullable = false)
    public UUID id;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "COM_DATA_COMPRA", nullable = false)
    public Date dataCompra;

    @Enumerated(EnumType.STRING)
    @Column(name = "COM_STATUS", nullable = false, length = 50)
    public StatusCompra status;

    @Enumerated(EnumType.STRING)
    @Column(name = "COM_METODO", nullable = false, length = 50)
    public Metodo metodo;

    @Enumerated(EnumType.STRING)
    @Column(name = "COM_PROCESSADOR", nullable = false, length = 50)
    public Processador processadorPagamento;

    @Column(name = "COM_PARCELAS", nullable = false)
    public int parcelas;

    @Column(name = "COM_VALOR_FINAL", nullable = false, precision = 10, scale = 2)
    public BigDecimal valorFinal;

    // Chaves Estrangeiras

    @ManyToOne
    @JoinColumn(name = "USU_ID", referencedColumnName = "USU_ID", nullable = false)
    public Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "OFE_ID", referencedColumnName = "id", nullable = false)
    public Oferta oferta;

    public Compra() {
    }
}
