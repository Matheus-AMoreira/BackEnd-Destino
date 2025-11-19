package com.destino.projeto_destino.config;

import com.destino.projeto_destino.services.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(
            JwtService jwtService
    ) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (request.getCookies() == null) {
            filterChain.doFilter(request, response);
            return;
        }
        String jwt = Arrays.stream(request.getCookies())
                .filter(cookie -> "jwt".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        try {
            final Claims claims = jwtService.extractAllClaims(jwt);

            List<GrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority((String) claims.get("role"))
            );

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(),
                    null,
                    authorities
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            SecurityContextHolder.clearContext();
            sendError(response, HttpStatus.UNAUTHORIZED, "Token expirado");
        } catch (MalformedJwtException e) {
            SecurityContextHolder.clearContext();
            sendError(response, HttpStatus.UNAUTHORIZED, "Token JWT inválido");
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            sendError(response, HttpStatus.UNAUTHORIZED, "Erro na autenticação");
        }
    }

    private void sendError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write("{error: %s, timestamp %s}".formatted(message, Instant.now()));
    }
}