package org.tech6.controller.testar;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/test")
@Produces(MediaType.TEXT_PLAIN)
public class TestController {

    @GET
    @Path("/publico")
    @PermitAll
    public String rotaPublica() {
        return "Olá! Esta é uma rota pública.";
    }

    @GET
    @Path("/privado/usuario")
    @RolesAllowed({ "ROLE_USUARIO", "ROLE_FUNCIONARIO", "ROLE_ADMINISTRADOR" })
    public String rotaUsuario() {
        return "Essa rota só usuário, funcionário e administrador tem acesso";
    }

    @GET
    @Path("/privado/funcionario")
    @RolesAllowed({ "ROLE_FUNCIONARIO", "ROLE_ADMINISTRADOR" })
    public String rotaPrivada() {
        return "Informação Secreta. Você acessou com um token de 'funcionário' ou 'adm' válido!";
    }

    @GET
    @Path("/privado/admin")
    @RolesAllowed("ROLE_ADMINISTRADOR")
    public String rotaAdmin() {
        return "Dados Ultra Secretos. Acesso concedido apenas para administradores!";
    }
}
