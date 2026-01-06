package com.fatec.destino.model.usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "ses_session")
public class SessionToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ses_id")
    private Long id;

    @Column(name = "ses_token")
    private String token;

    @Column(name = "ses_validade")
    private Long validade;

    @OneToOne
    @JoinColumn(name = "USU_ID", referencedColumnName = "USU_ID", nullable = false)
    private Usuario usuario;

    public SessionToken() {
    }

    public SessionToken(String token, Long validade, Usuario usuario) {
        this.token = token;
        this.validade = validade;
        this.usuario = usuario;
    }
}
