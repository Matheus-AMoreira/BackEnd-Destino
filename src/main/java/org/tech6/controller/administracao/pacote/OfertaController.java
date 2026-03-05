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
        return Response.ok(ofertaService.registrarOferta(dto)).build();
    }
}
