package com.destino.projeto_destino.model;

import com.destino.projeto_destino.model.UsuarioUtils.Cpf.Cpf;
import com.destino.projeto_destino.model.UsuarioUtils.Cpf.CpfConverter;
import com.destino.projeto_destino.model.UsuarioUtils.Email.Email;
import com.destino.projeto_destino.model.UsuarioUtils.Email.EmailConverter;
import com.destino.projeto_destino.model.UsuarioUtils.Telefone.Telefone;
import com.destino.projeto_destino.model.UsuarioUtils.Telefone.TelefoneConverter;
import com.destino.projeto_destino.model.UsuarioUtils.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Table(name = "usu_usuario")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, name = "USU_ID")
    private UUID id;

    @Column(nullable = false, name = "USU_NOME", length = 100)
    private String nome;

    @Column(nullable = false, name = "USU_CPF", unique = true, length = 11)
    @Convert(converter = CpfConverter.class)
    private Cpf cpf;

    @Column(nullable = false, name = "USU_EMAIL", unique = true, length = 100)
    @Convert(converter = EmailConverter.class)
    private Email email;

    @Column(nullable = false, name = "USU_TELEFONE", length = 11)
    @Convert(converter = TelefoneConverter.class)
    private Telefone telefone;

    @Column(nullable = false, name = "USU_SENHA", length = 100)
    private String senha;

    @Column(nullable = false, name = "USU_PERFIL")
    @Enumerated(EnumType.STRING)
    private UserRole perfil;

    @Column(nullable = false, name = "USU_VALIDO")
    private Boolean valido;

    @UpdateTimestamp
    @Column(updatable = false, name = "USU_DATA_ATUALIZACAO")
    private Date atualizacao;

    @CreationTimestamp
    @Column(nullable = false, name = "USU_DATA_CADASTRO")
    private Date cadastro;

    public Usuario(){}

    public Usuario(String nome, Cpf cpf,  Email email, Telefone telefone, String senha, UserRole perfil, Boolean valido) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.senha = senha;
        this.perfil = perfil;
        this.valido = valido;
    }

    // Métodos de UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.perfil.name()));
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email.getValor();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.valido;
    }

    // Getters e Setters

    public UUID getId() {
        return id;
    }

    public String nome() {
        return this.nome;
    }

    public String getCpf() {
        return cpf != null ? cpf.getValorFormatado() : null;
    }

    public String getEmail() {
        return email != null ? this.email.getValor() : null;
    }

    public String getTelefone() {
        return telefone != null ? this.telefone.getValorFormatado() : null;
    }

    public UserRole getPerfil() {
        return perfil;
    }

    public Boolean getValido() {
        return this.valido;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCpf(String cpf) { this.cpf = new Cpf(cpf);}

    public void setEmail(String email) {this.email = new Email(email);}

    public void setTelefone(String telefone) {
        this.telefone = new Telefone(telefone);
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setPerfil(UserRole perfil) {
        this.perfil = perfil;
    }

    public void setValido(Boolean valido) {
        this.valido = valido;
    }
}
