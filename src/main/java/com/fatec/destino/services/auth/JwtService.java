package com.fatec.destino.services.auth;

import com.fatec.destino.model.usuario.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final Key secretKey;

    public JwtService(@Value("${security.jwt.secret-key:#{null}}") String secret) {
        this.secretKey = pegarChave(secret);
    }

    private Key pegarChave(String secret) {
        if (secret == null || secret.isEmpty()) {
            Key key = Jwts.SIG.HS512.key().build();
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
        List<String> authorities = usuario.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts
                .builder()
                .header().add("typ", "JWT").and()
                .subject(usuario.getUsername())
                .claim("role", "ROLE_" + usuario.getAuthorities())
                .claim("authorities", authorities)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
