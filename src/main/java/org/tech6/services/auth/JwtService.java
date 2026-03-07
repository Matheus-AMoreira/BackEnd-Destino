package org.tech6.services.auth;

import org.tech6.model.usuario.Usuario;
import org.tech6.util.model.usuario.perfil.UsuarioAuthority;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class JwtService {

    public String generateToken(Usuario usuario, long expirationInSeconds) {
        Set<String> groups = new HashSet<>();

        // Add roles as groups (e.g., ROLE_ADMINISTRADOR)
        groups.add("ROLE_" + usuario.role.name());

        // Add granular authorities as groups
        Set<String> authorities = usuario.role.authorities.stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
        groups.addAll(authorities);

        return Jwt.issuer("https://localhost")
                .upn(usuario.email)
                .groups(groups)
                .expiresIn(expirationInSeconds)
                .sign();
    }
}
