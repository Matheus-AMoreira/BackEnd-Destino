package org.tech6.controller.pacotes;

import org.tech6.dto.pacote.PacoteResponseDTO;
import org.tech6.model.pacote.Pacote;
import org.tech6.services.pacote.PacoteService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Path("/api/publico/pacote")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PacotePublicoController {

    private final PacoteService pacoteService;

    public PacotePublicoController(PacoteService pacoteService) {
        this.pacoteService = pacoteService;
    }

    @GET
    public Response getPacotes(
            @QueryParam("nome") String nome,
            @QueryParam("precoMax") BigDecimal precoMax,
            @DefaultValue("0") @QueryParam("page") int page,
            @DefaultValue("12") @QueryParam("size") int size) {
        return Response.ok(pacoteService.buscarPacotesComFiltros(nome, precoMax, page, size)).build();
    }

    // Top Destinos (baseado em vendas)
    @GET
    @Path("/mais-vendidos")
    public Response getPacotesMaisVendidos() {
        return Response.ok(pacoteService.pacotesMaisvendidos()).build();
    }

    @GET
    @Path("/detalhes/{nome}")
    public Response getPacotePorNomeUrl(@PathParam("nome") String nome) {
        // Decodifica caso venha com caracteres especiais (espaço, acentos)
        String nomeDecodificado = URLDecoder.decode(nome, StandardCharsets.UTF_8);
        Pacote pacote = pacoteService.pegarPacotePorNomeExato(nomeDecodificado);
        if (pacote == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(pacote).build();
    }

    @GET
    @Path("/buscar/{nome}")
    public Response getPacoteName(@PathParam("nome") String nome) {
        String nomeDecodificado = URLDecoder.decode(nome, StandardCharsets.UTF_8);
        return Response.ok(pacoteService.pegarPacotesPorNome(nomeDecodificado)).build();
    }

    @GET
    @Path("/{id}")
    public Response getPacotePorId(@PathParam("id") long id) {
        Pacote pacote = pacoteService.pegarPacotePorId(id);
        if (pacote == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(pacote).build();
    }
}
