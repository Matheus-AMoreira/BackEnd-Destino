package com.fatec.destino.model.usuario

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "ses_session")
class SessionToken (
    @Column(name = "ses_token")
    var token: String,

    @Column(name = "ses_validade")
    var validade: Long,

    @OneToOne
    @JoinColumn(name = "USU_ID", referencedColumnName = "USU_ID", nullable = false)
    var usuario: Usuario
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ses_id")
    var id: Long? = null
}