package org.tech6.controller.administracao.pacote;

import org.tech6.dto.pacote.OfertaRegistroDTO;
import org.tech6.services.pacote.OfertaService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/pacote/oferta")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OfertaController {

    private final OfertaService ofertaService;

    public OfertaController(OfertaService ofertaService) {
        this.ofertaService = ofertaService;
    }

    @POST
    @Path("/registrar")
    @RolesAllowed({ "ROLE_ADMINISTRADOR", "ROLE_FUNCIONARIO" })
    public Response registrarOferta(OfertaRegistroDTO dto) {
        try {
            return Response.ok(ofertaService.registrarOferta(dto)).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "ROLE_ADMINISTRADOR", "ROLE_FUNCIONARIO" })
    public Response atualizarOferta(@PathParam("id") long id, OfertaRegistroDTO dto) {
        try {
            return Response.ok(ofertaService.atualizarOferta(id, dto)).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "ROLE_ADMINISTRADOR", "ROLE_FUNCIONARIO" })
    public Response deletarOferta(@PathParam("id") long id) {
        try {
            return Response.ok(ofertaService.deletarOferta(id)).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
