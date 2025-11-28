package com.destino.projeto_destino.model.usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Setter;

import java.time.Instant;

@Entity
@Setter
@Table(name = "ses_session")
public class SessionToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ses_id")
    private Long id;

    @Column(name = "ses_token")
    private String token;

    @Column(name = "ses_validade")
    private Instant validade;

    @OneToOne
    @JoinColumn(name = "USU_ID", referencedColumnName = "USU_ID", nullable = false)
    private Usuario usuario;

    public SessionToken() {
    }

    public SessionToken(String token, Instant validade, Usuario usuario) {
        this.token = token;
        this.validade = validade;
        this.usuario = usuario;
    }
}
