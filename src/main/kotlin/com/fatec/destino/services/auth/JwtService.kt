package com.fatec.destino.services.auth

import com.fatec.destino.model.usuario.Usuario
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Service
import java.security.Key
import java.util.Base64
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService(@Value("\${security.jwt.secret-key:#{null}}") secret: String?) {
    private val secretKey: Key

    init {
        this.secretKey = pegarChave(secret)
    }

    private fun pegarChave(secret: String?): Key {
        if (secret == null || secret.isEmpty()) {
            val key: Key = Jwts.SIG.HS512.key().build()
            val encoder = Base64.getUrlEncoder().withoutPadding()
            println("🔑 Chave secreta gerada: " + encoder.encodeToString(key.getEncoded()))
            return key
        } else {
            println("🔑 Chave secreta encontrada nas configurações")
            return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret))
        }
    }

    fun generateToken(
        usuario: Usuario,
        expiration: Long
    ): String? {
        val authorities = usuario.getAuthorities().stream()
            .map<String?> { obj: GrantedAuthority? -> obj!!.getAuthority() }
            .toList()

        return Jwts
            .builder()
            .header().add("typ", "JWT").and()
            .subject(usuario.getUsername())
            .claim("role", "ROLE_" + usuario.getAuthorities())
            .claim("authorities", authorities)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + expiration))
            .signWith(secretKey)
            .compact()
    }

    fun extractAllClaims(token: String?): Claims? {
        return Jwts.parser()
            .verifyWith(secretKey as SecretKey?)
            .build()
            .parseSignedClaims(token)
            .getPayload()
    }
}