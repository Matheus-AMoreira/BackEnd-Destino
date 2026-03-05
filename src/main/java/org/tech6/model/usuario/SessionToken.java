package org.tech6.model.usuario;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ses_session")
public class SessionToken extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ses_id")
    public Long id;

    @Column(name = "ses_token")
    public String token;

    @Column(name = "ses_validade")
    public Long validade;

    @OneToOne
    @JoinColumn(name = "USU_ID", referencedColumnName = "USU_ID", nullable = false)
    public Usuario usuario;

    public SessionToken() {
    }

    public SessionToken(String token, Long validade, Usuario usuario) {
        this.token = token;
        this.validade = validade;
        this.usuario = usuario;
    }
}
