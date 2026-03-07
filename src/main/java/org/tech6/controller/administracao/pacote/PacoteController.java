package org.tech6.controller.administracao.pacote;

import org.tech6.dto.pacote.PacoteRegistroDTO;
import org.tech6.model.pacote.Pacote;
import org.tech6.services.pacote.PacoteService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("/api/pacote")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PacoteController {

    private final PacoteService pacoteService;

    public PacoteController(PacoteService pacoteService) {
        this.pacoteService = pacoteService;
    }

    @GET
    @Path("/agrupado-admin")
    @RolesAllowed({"ROLE_FUNCIONARIO", "ROLE_ADMINISTRADOR"})
    public Response getPacotesAdmin() {
        return Response.ok(pacoteService.pegarPacotesAgrupadosPorLocal()).build();
    }

    @POST
    @RolesAllowed({"ROLE_FUNCIONARIO", "ROLE_ADMINISTRADOR"})
    public Response registrarPacote(PacoteRegistroDTO pacoteRegistroDTO) {
        return Response.ok(pacoteService.criarPacote(pacoteRegistroDTO)).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"ROLE_FUNCIONARIO", "ROLE_ADMINISTRADOR"})
    public Response atualizarPacote(@PathParam("id") int id, PacoteRegistroDTO pacoteRegistroDTO) {
        return Response.ok(pacoteService.atualizarPacote(id, pacoteRegistroDTO)).build();
    }
}