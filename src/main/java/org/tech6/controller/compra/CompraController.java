package org.tech6.controller.compra;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.tech6.dto.compra.CompraRequestDTO;
import org.tech6.dto.compra.CompraResponseDTO;
import org.tech6.dto.compra.ViagemDetalhadaDTO;
import org.tech6.dto.compra.ViagemResumoDTO;
import org.tech6.services.compra.CompraService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/compra")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({ "ROLE_USUARIO", "ROLE_FUNCIONARIO", "ROLE_ADMINISTRADOR" })
public class CompraController {

    private final CompraService compraService;

    @Inject
    JsonWebToken jwt;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @GET
    @Path("/andamento")
    public Response listarViagensEmAndamento() {
        String email = jwt.getName();
        return Response.ok(compraService.listarViagensEmAndamentoDoUsuario(email)).build();
    }

    @GET
    @Path("/concluidas")
    public Response listarViagensComcluidas() {
        String email = jwt.getName();
        return Response.ok(compraService.listarViagensConcluidasDoUsuarios(email)).build();
    }

    @GET
    @Path("/{id}")
    public Response detalharViagem(@PathParam("id") Long id) {
        String email = jwt.getName();
        return Response.ok(compraService.buscarDetalhesViagem(id, email)).build();
    }

    @POST
    public Response realizarCompra(CompraRequestDTO dto) {
        return Response.ok(compraService.processarCompra(dto)).build();
    }
}
