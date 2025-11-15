package com.destino.projeto_destino.model.usuario;

import com.destino.projeto_destino.util.usuario.Cpf.Cpf;
import com.destino.projeto_destino.util.usuario.Cpf.CpfConverter;
import com.destino.projeto_destino.util.usuario.Email.Email;
import com.destino.projeto_destino.util.usuario.Email.EmailConverter;
import com.destino.projeto_destino.util.usuario.Telefone.Telefone;
import com.destino.projeto_destino.util.usuario.Telefone.TelefoneConverter;
import com.destino.projeto_destino.util.usuario.perfil.UserRole;
import jakarta.persistence.*;
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

    @Column(nullable = false, name = "USU_NOME", length = 20)
    private String nome;

    @Column(nullable = false, name = "USU_SOBRENOME", length = 20)
    private String sobreNome;

    @Column(nullable = false, name = "USU_CPF", unique = true, length = 11)
    @Convert(converter = CpfConverter.class)
    private Cpf cpf;

    @Column(nullable = false, name = "USU_EMAIL", unique = true, length = 50)
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

    public Usuario(String nome, String sobreNome, Cpf cpf, Email email, Telefone telefone, String senha, UserRole perfil, Boolean valido) {
        this.nome = nome;
        this.sobreNome = sobreNome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.senha = senha;
        this.perfil = perfil;
        this.valido = valido;
    }

    public Usuario() {

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

    public String getNome() {
        return this.nome;
    }

    public String getSobreNome() {
        return this.sobreNome;
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

    public Date getAtualizacao() {
        return atualizacao;
    }

    public Date getCadastro() {
        return cadastro;
    }

    public void setNome(String nome) {this.nome = nome;}

    public void setSobreNome(String sobreNome){this.sobreNome = sobreNome;}

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

    public void setAtualizacao(Date atualizacao) {
        this.atualizacao = atualizacao;
    }

    public void setCadastro(Date cadastro) {
        this.cadastro = cadastro;
    }
}
