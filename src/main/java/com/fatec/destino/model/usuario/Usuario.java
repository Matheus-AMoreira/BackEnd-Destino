package com.fatec.destino.model.usuario;

import com.fatec.destino.util.model.usuario.Cpf.Cpf;
import com.fatec.destino.util.model.usuario.Cpf.CpfConverter;
import com.fatec.destino.util.model.usuario.Telefone.Telefone;
import com.fatec.destino.util.model.usuario.Telefone.TelefoneConverter;
import com.fatec.destino.util.model.usuario.perfil.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
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
    @Email(message = "Precisa de um email válido!")
    private String email;

    @Column(nullable = false, name = "USU_TELEFONE", length = 11)
    @Convert(converter = TelefoneConverter.class)
    private Telefone telefone;

    @JsonIgnoreProperties
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

    public Usuario() {

    }

    public Usuario(String nome, String sobreNome, String cpf, String email, String telefone, String senha, UserRole perfil, Boolean valido) {
        this.nome = nome;
        this.sobreNome = sobreNome;
        this.cpf = new Cpf(cpf);
        this.email = email;
        this.telefone = new Telefone(telefone);
        this.senha = senha;
        this.perfil = perfil;
        this.valido = valido;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isEnabled() {
        return this.valido;
    }
}
