package org.tech6.controller.usuario;

import org.tech6.dto.auth.login.LoginResponseDTO;
import org.tech6.dto.auth.login.LoginUsuarioDto;
import org.tech6.dto.auth.registro.RegistrationResponseDto;
import org.tech6.dto.auth.registro.RegistroDto;
import org.tech6.services.auth.AuthenticationService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.Map;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    private final AuthenticationService authenticationService;
    private static final String COOKIE_NAME = "session_token";

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @POST
    @Path("/cadastrar")
    public RestResponse<RegistrationResponseDto> cadastrar(@Valid RegistroDto usuario) {
        RegistrationResponseDto resposta = authenticationService.cadastrarUsuario(usuario);

        if (resposta.erro()) {
            return RestResponse.ok(resposta);
        }
        return RestResponse.status(RestResponse.Status.CREATED, resposta);
    }

    @POST
    @Path("/entrar")
    public Response entrar(@Valid LoginUsuarioDto loginDto) {
        AuthenticationService.LoginResult result = new AuthenticationService.LoginResult();
        LoginResponseDTO resposta = authenticationService.realizarLogin(loginDto, result);

        Response.ResponseBuilder builder;
        if (resposta.erro()) {
            builder = Response.status(Response.Status.UNAUTHORIZED).entity(resposta);
        } else {
            builder = Response.ok(resposta);
        }

        if (result.cookie != null) {
            builder.cookie(result.cookie);
        }

        return builder.build();
    }

    @POST
    @Path("/renovar-token")
    public Response renovarToken(@CookieParam(COOKIE_NAME) Cookie sessionCookie) {
        if (sessionCookie == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new LoginResponseDTO(true, "Cookie de sessão não encontrado.", null))
                    .build();
        }

        AuthenticationService.LoginResult result = new AuthenticationService.LoginResult();
        LoginResponseDTO resposta = authenticationService.renovarToken(sessionCookie.getValue(), result);

        Response.ResponseBuilder builder;
        if (resposta.erro()) {
            builder = Response.status(Response.Status.UNAUTHORIZED).entity(resposta);
        } else {
            builder = Response.ok(resposta);
        }

        if (result.cookie != null) {
            builder.cookie(result.cookie);
        }

        return builder.build();
    }

    @POST
    @Path("/sair")
    public Response sair(@CookieParam(COOKIE_NAME) Cookie sessionCookie) {
        AuthenticationService.LoginResult result = new AuthenticationService.LoginResult();
        authenticationService.realizarLogout(sessionCookie != null ? sessionCookie.getValue() : null, result);

        return Response.ok(new LoginResponseDTO(false, "Logout realizado com sucesso.", null))
                .cookie(result.cookie)
                .build();
    }
}