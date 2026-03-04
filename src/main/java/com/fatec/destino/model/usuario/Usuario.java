package com.fatec.destino.model.usuario;

import com.fatec.destino.util.model.usuario.Cpf.Cpf;
import com.fatec.destino.util.model.usuario.Cpf.CpfConverter;
import com.fatec.destino.util.model.usuario.Telefone.Telefone;
import com.fatec.destino.util.model.usuario.Telefone.TelefoneConverter;
import com.fatec.destino.util.model.usuario.perfil.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fatec.destino.util.model.usuario.perfil.UsuarioAuthority;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "USU_USUARIO")
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

    @Column(nullable = false, name = "USU_VALIDO")
    private Boolean valido;

    @Enumerated(EnumType.STRING)
    @Column(name = "USU_ROLE")
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "USU_AUTHORITY")
    private List<UsuarioAuthority> authorities;

    @UpdateTimestamp
    @Column(updatable = false, name = "USU_DATA_ATUALIZACAO")
    private Date atualizacao;

    @CreationTimestamp
    @Column(nullable = false, name = "USU_DATA_CADASTRO")
    private Date cadastro;

    public Usuario(
            String nome,
            String sobreNome,
            String cpf,
            String email,
            String telefone,
            String senha,
            Boolean valido,
            UserRole role,
            List<UsuarioAuthority> authority
            ) {
        this.nome = nome;
        this.sobreNome = sobreNome;
        this.cpf = new Cpf(cpf);
        this.email = email;
        this.telefone = new Telefone(telefone);
        this.senha = senha;
        this.valido = valido;
        this.role = role;
        this.authorities = authority;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authoritiesSet = new HashSet<>();

        // 1. Adiciona a Role (ex: ROLE_ADMIN)
        authoritiesSet.add(new SimpleGrantedAuthority("ROLE_" + this.role.name()));

        // 2. Pega as permissões padrão da "Árvore" do Enum
        this.role.getAuthorities().forEach(authName -> {
            authoritiesSet.add(new SimpleGrantedAuthority(authName.name()));
        });

        // 3. Adiciona as permissões exclusivas (se houver na tabela USU_AUTHORITIES)
        if (this.authorities != null) {
            this.authorities.forEach(auth -> {
                authoritiesSet.add(new SimpleGrantedAuthority(auth.name()));
            });
        }

        return authoritiesSet;
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
