package org.tech6.controller.administracao.usuario;

import org.tech6.dto.usuario.UsuarioDTO;
import org.tech6.dto.auth.validar.ValidarResponseDTO;
import org.tech6.model.usuario.Usuario;
import org.tech6.services.usuario.UsuarioService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

@Path("/api/usuario")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("ROLE_FUNCIONARIO")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GET
    @Path("/invalidos")
    public Response inValiduser() {
        return Response.ok(usuarioService.inValidUsers()).build();
    }

    @PATCH
    @Path("/validar/{id}")
    public Response validUser(@PathParam("id") UUID id) {
        return Response.ok(usuarioService.validar(id)).build();
    }

    @GET
    public Response buscarUsuarios() {
        return Response.ok(usuarioService.buscarUsuarios()).build();
    }
}
