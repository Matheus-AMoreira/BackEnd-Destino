package com.destino.projeto_destino.services.auth;

import com.destino.projeto_destino.model.usuario.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    private final Key secretKey;

    public JwtService(@Value("${security.jwt.secret-key:#{null}}") String secret) {
        this.secretKey = pegarChave(secret);
    }

    private Key pegarChave(String secret) {
        if (secret == null || secret.isEmpty()) {
            Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
            System.out.println("🔑 Chave secreta gerada: " + encoder.encodeToString(key.getEncoded()));
            return key;
        } else {
            System.out.println("🔑 Chave secreta encontrada nas configurações");
            return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        }
    }

    public String generateToken(
            Usuario usuario,
            long expiration
    ) {
        return Jwts
                .builder()
                .setSubject(usuario.getEmail())
                .claim("role", "ROLE_" + usuario.getPerfil())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
