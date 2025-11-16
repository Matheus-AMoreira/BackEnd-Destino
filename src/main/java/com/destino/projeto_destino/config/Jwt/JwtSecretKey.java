package com.destino.projeto_destino.config.Jwt;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.security.Key;
import java.util.Base64;

@Configuration
public class JwtSecretKey {
    private static final int KEY_SIZE_BITS = 256;
    @Value("${security.jwt.secret-key:#{null}}")
    private String secretKey;

    public Key pegarChave() {

        if (secretKey == null || secretKey.isEmpty()) {
            Key key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
            this.secretKey = Base64.getEncoder().encodeToString(key.getEncoded());

            System.out.println("🔑 Chave secreta Gerada: " + secretKey);
        } else {
            System.out.println("🔑 Chave secreta encontrada nas configurações");
        }
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
