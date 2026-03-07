package org.tech6.config;

import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class RoleAugmentor implements SecurityIdentityAugmentor {

    // Define a hierarquia: quem tem a chave, ganha os valores da lista
    private static final Map<String, List<String>> HIERARCHY = Map.of(
            "ROLE_ADMINISTRADOR", List.of("ROLE_FUNCIONARIO", "ROLE_USUARIO"),
            "ROLE_FUNCIONARIO", List.of("ROLE_USUARIO")
    );

    @Override
    public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {
        if (identity.isAnonymous()) {
            return Uni.createFrom().item(identity);
        }

        QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder(identity);

        // Percorre os papéis atuais e adiciona os herdados com base no mapa
        identity.getRoles().forEach(role -> {
            if (HIERARCHY.containsKey(role)) {
                HIERARCHY.get(role).forEach(builder::addRole);
            }
        });

        return Uni.createFrom().item(builder.build());
    }
}
