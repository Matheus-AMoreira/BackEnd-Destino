package com.fatec.destino.config

import com.fatec.destino.services.auth.JwtService
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.time.Instant

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        val jwt: String

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        jwt = authHeader.substring(7)

        try {
            val claims = jwtService.extractAllClaims(jwt)

            if (claims?.subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                val grantedAuthorities: MutableList<GrantedAuthority?> = ArrayList<GrantedAuthority?>()
                grantedAuthorities.add(SimpleGrantedAuthority((claims["role"] as kotlin.String?)!!))
                val authoritiesClaim = claims["authorities"] as MutableList<String>?

                if (authoritiesClaim != null) {
                    for (auth in authoritiesClaim) {
                        grantedAuthorities.add(SimpleGrantedAuthority(auth))
                    }
                }

                val authToken = UsernamePasswordAuthenticationToken(
                    claims.getSubject(),
                    null,
                    grantedAuthorities as Collection<out GrantedAuthority>
                )
                println(authToken)
                SecurityContextHolder.getContext().authentication = authToken
            }
        } catch (_: ExpiredJwtException) {
            SecurityContextHolder.clearContext()
            response.status = HttpStatus.UNAUTHORIZED.value()
            response.writer.write("{\"error\": \"Token expirado\", \"code\": \"TOKEN_EXPIRED\"}")
            filterChain.doFilter(request, response)
            return
        } catch (e: MalformedJwtException) {
            SecurityContextHolder.clearContext()
            sendError(response, "Token JWT inválido")
            return
        } catch (_: Exception) {
            SecurityContextHolder.clearContext()
            sendError(response, "Erro na autenticação")
            return
        }

        filterChain.doFilter(request, response)
    }

    @Throws(IOException::class)
    private fun sendError(response: HttpServletResponse, message: String?) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = "application/json"
        response.writer.write("""{"error": "$message", "timestamp": "${Instant.now()}"}""")
    }
}