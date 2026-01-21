package com.fatec.destino.config

import com.fatec.destino.repository.usuario.UsuarioRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfiguration(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val userRepository: UsuarioRepository
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            cors { configurationSource = corsConfigurationSource() }
            csrf { disable() }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            formLogin { disable() }

            addFilterBefore<BasicAuthenticationFilter>(jwtAuthenticationFilter)

            authorizeHttpRequests {
                // Swagger
                authorize("/swagger-ui/**", permitAll)
                authorize("/v3/api-docs/**", permitAll)

                // Endpoints Públicos
                authorize("/api/compra/**", permitAll)
                authorize("/api/test/publico/**", permitAll)
                authorize("/api/publico/pacote/**", permitAll)

                // AuthController
                authorize("/api/auth/cadastrar", permitAll)
                authorize("/api/auth/entrar", permitAll)
                authorize("/api/auth/renovar-token", permitAll)
                authorize("/api/auth/sair", permitAll)

                // Roles Específicas
                authorize("/api/compra/**", hasRole("USUARIO"))

                listOf(
                    "/api/pacote/**",
                    "/api/pacote/{id}",
                    "/api/usuario/**",
                    "/api/pacote/agrupado-admin",
                    "/api/hotel/**",
                    "/api/transporte/**",
                    "/api/dashboard/**",
                    "/api/pacote-foto/**"
                ).forEach { path -> authorize(path, hasRole("FUNCIONARIO")) }

                // Endpoints Protegidos
                authorize("/api/usuario/**", authenticated)
                authorize("/api/test/privado/**", authenticated)

                authorize(anyRequest, authenticated)
            }
        }
        return http.build()
    }

    @Bean
    fun roleHierarchy(): RoleHierarchy {
        return RoleHierarchyImpl.fromHierarchy(
            """
            ROLE_ADMINISTRADOR > ROLE_FUNCIONARIO
            ROLE_FUNCIONARIO > ROLE_USUARIO
            """.trimIndent()
        )
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = listOf("https://localhost:5173")
            allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
            allowCredentials = true
        }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
    }

    @Bean
    fun userDetailsService(): UserDetailsService = UserDetailsService { email ->
        userRepository.findByEmail(email)
            .orElseThrow { UsernameNotFoundException("Email não encontrado: $email") }
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder(12)

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }
}