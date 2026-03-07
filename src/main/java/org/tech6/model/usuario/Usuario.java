package org.tech6.model.usuario;

import org.tech6.util.model.usuario.Cpf.Cpf;
import org.tech6.util.model.usuario.Cpf.CpfConverter;
import org.tech6.util.model.usuario.Telefone.Telefone;
import org.tech6.util.model.usuario.Telefone.TelefoneConverter;
import org.tech6.util.model.usuario.perfil.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.tech6.util.model.usuario.perfil.UsuarioAuthority;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.*;

@Entity
@Table(name = "USU_USUARIO")
public class Usuario extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, name = "USU_ID")
    public UUID id;

    @Column(nullable = false, name = "USU_NOME", length = 20)
    public String nome;

    @Column(nullable = false, name = "USU_SOBRENOME", length = 20)
    public String sobreNome;

    @Column(nullable = false, name = "USU_CPF", unique = true, length = 11)
    @Convert(converter = CpfConverter.class)
    public Cpf cpf;

    @Column(nullable = false, name = "USU_EMAIL", unique = true, length = 50)
    @Email(message = "Precisa de um email válido!")
    public String email;

    @Column(nullable = false, name = "USU_TELEFONE", length = 11)
    @Convert(converter = TelefoneConverter.class)
    public Telefone telefone;

    @JsonIgnoreProperties
    @Column(nullable = false, name = "USU_SENHA", length = 100)
    public String senha;

    @Column(nullable = false, name = "USU_VALIDO")
    public Boolean valido;

    @Enumerated(EnumType.STRING)
    @Column(name = "USU_ROLE", nullable = false)
    public UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "USU_AUTHORITY")
    public List<UsuarioAuthority> authorities;

    @UpdateTimestamp
    @Column(updatable = false, name = "USU_DATA_ATUALIZACAO")
    public Date atualizacao;

    @CreationTimestamp
    @Column(nullable = false, name = "USU_DATA_CADASTRO")
    public Date cadastro;

    public Usuario() {
    }

    public Usuario(
            String nome,
            String sobreNome,
            String cpf,
            String email,
            String telefone,
            String senha,
            Boolean valido,
            UserRole role,
            List<UsuarioAuthority> authority) {
        this.nome = nome;
        this.sobreNome = sobreNome;
        if (cpf != null)
            this.cpf = new Cpf(cpf);
        this.email = email;
        if (telefone != null)
            this.telefone = new Telefone(telefone);
        this.senha = senha;
        this.valido = valido;
        this.role = role;
        this.authorities = authority;
    }
}
