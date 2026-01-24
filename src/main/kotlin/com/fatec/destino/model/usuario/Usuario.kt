package com.fatec.destino.model.usuario

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fatec.destino.util.model.usuario.cpf.Cpf
import com.fatec.destino.util.model.usuario.cpf.CpfConverter
import com.fatec.destino.util.model.usuario.perfil.UserRole
import com.fatec.destino.util.model.usuario.telefone.Telefone
import com.fatec.destino.util.model.usuario.telefone.TelefoneConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Email
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

@Entity
@Table(name = "usu_usuario")
class Usuario (
    @Column(nullable = false, name = "USU_NOME", length = 20)
    var nome: String,

    @Column(nullable = false, name = "USU_SOBRENOME", length = 20)
    var sobreNome: String,

    @Column(nullable = false, name = "USU_CPF", unique = true, length = 11)
    @Convert(converter = CpfConverter::class)
    var cpf: Cpf,

    @Column(nullable = false, name = "USU_EMAIL", unique = true, length = 50)
    var email: @Email(message = "Precisa de um email válido!") String,

    @Column(nullable = false, name = "USU_TELEFONE", length = 11)
    @Convert(converter = TelefoneConverter::class)
    var telefone: Telefone? = null,

    @JsonIgnoreProperties
    @Column(nullable = false, name = "USU_SENHA", length = 100)
    var senha: String?,

    @Column(nullable = false, name = "USU_PERFIL")
    @Enumerated(EnumType.STRING)
    var perfil: UserRole = UserRole.USUARIO,

    @Column(nullable = false, name = "USU_VALIDO")
    var valido: Boolean = false

) : UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, name = "USU_ID")
    var id: UUID? = null

    @UpdateTimestamp
    @Column(updatable = false, name = "USU_DATA_ATUALIZACAO")
    var atualizacao: Date? = null

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "USU_DATA_CADASTRO")
    var cadastro: Date? = null

    override fun getAuthorities(): Collection<GrantedAuthority> = listOfNotNull(perfil)

    override fun getPassword(): String? = senha

    override fun getUsername(): String = email

    override fun isEnabled(): Boolean = valido
}