package org.tech6.controller.administracao.pacote;

import org.tech6.dto.pacote.pacoteFoto.PacoteFotoRegistroDTO;
import org.tech6.model.pacote.pacoteFoto.PacoteFoto;
import org.tech6.services.pacote.PacoteFotoService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/pacote-foto")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("ROLE_FUNCIONARIO")
public class PacoteFotoController {

    private final PacoteFotoService service;

    public PacoteFotoController(PacoteFotoService service) {
        this.service = service;
    }

    @GET
    public Response listar() {
        // Assuming service.listarPacotesFoto() now returns List<PacoteFoto> directly
        return Response.ok(service.listarPacotesFoto()).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") long id) {
        // Assuming service.buscarPorId() now returns PacoteFoto directly
        return Response.ok(service.buscarPorId(id)).build();
    }

    @POST
    public Response criar(PacoteFotoRegistroDTO dto) {
        // Assuming service.criarPacoteFoto() now returns String directly
        return Response.ok(service.criarPacoteFoto(dto)).build();
    }

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") long id, PacoteFotoRegistroDTO dto) {
        // Assuming service.atualizarPacoteFoto() now returns String directly
        return Response.ok(service.atualizarPacoteFoto(id, dto)).build();
    }
}
