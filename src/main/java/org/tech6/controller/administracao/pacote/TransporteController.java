package org.tech6.controller.administracao.pacote;

import org.tech6.dto.pacote.transporte.TransporteRegistroDTO;
import org.tech6.model.pacote.transporte.Transporte;
import org.tech6.services.pacote.TransporteService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/transporte")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"ROLE_FUNCIONARIO", "ROLE_ADMINISTRADOR"})
public class TransporteController {
    private final TransporteService transporteService;

    public TransporteController(TransporteService transporteService) {
        this.transporteService = transporteService;
    }

    @GET
    public Response getTransporte() {
        return Response.ok(transporteService.pegarTransportes()).build();
    }

    @GET
    @Path("/{id}")
    public Response getTransporteById(@PathParam("id") int id) {
        return Response.ok(transporteService.pegarTransportePorId(id)).build();
    }

    @POST
    public Response registrarTransporte(TransporteRegistroDTO transporte) {
        return Response.ok(transporteService.criarTransportes(transporte)).build();
    }

    @PUT
    @Path("/{id}")
    public Response atualizarTransporte(@PathParam("id") int id, TransporteRegistroDTO dto) {
        return Response.ok(transporteService.atualizarTransporte(id, dto)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deletarTransporte(@PathParam("id") int id) {
        return Response.ok(transporteService.deletarTransporte(id)).build();
    }
}
