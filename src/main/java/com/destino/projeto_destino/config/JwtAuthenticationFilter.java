package com.destino.projeto_destino.config;

import com.destino.projeto_destino.services.auth.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
import java.util.ArrayList;
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

        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            final Claims claims = jwtService.extractAllClaims(jwt);

            if (claims.getSubject() != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
                grantedAuthorities.add(new SimpleGrantedAuthority((String) claims.get("role")));
                List<String> authoritiesClaim = (List<String>) claims.get("authorities");

                if (authoritiesClaim != null) {
                    for (String auth : authoritiesClaim) {
                        grantedAuthorities.add(new SimpleGrantedAuthority(auth));
                    }
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        claims.getSubject(),
                        null,
                        grantedAuthorities
                );
                System.out.println(authToken);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        } catch (ExpiredJwtException e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("{\"error\": \"Token expirado\", \"code\": \"TOKEN_EXPIRED\"}");
            filterChain.doFilter(request, response);
            return;
        } catch (MalformedJwtException e) {
            SecurityContextHolder.clearContext();
            sendError(response, HttpStatus.UNAUTHORIZED, "Token JWT inválido");
            return;
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            sendError(response, HttpStatus.UNAUTHORIZED, "Erro na autenticação");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write("{error: %s, timestamp %s}".formatted(message, Instant.now()));
    }
}