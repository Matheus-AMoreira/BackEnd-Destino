package org.tech6.controller.administracao.pacote;

import org.tech6.dto.pacote.hotel.HotelRegistroDTO;
import org.tech6.model.pacote.hotel.Hotel;
import org.tech6.model.pacote.hotel.cidade.Cidade;
import org.tech6.model.pacote.hotel.cidade.estado.Estado;
import org.tech6.model.pacote.hotel.cidade.estado.regiao.Regiao;
import org.tech6.services.pacote.HotelService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/hotel")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("FUNCIONARIO")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GET
    public Response procurarHoteis() {
        return Response.ok(hotelService.pegarHoteis()).build();
    }

    // Endpoint para buscar hotel específico para edição
    @GET
    @Path("/{id}")
    public Response getHotelById(@PathParam("id") long id) {
        return Response.ok(hotelService.pegarHotelById(id)).build();
    }

    @GET
    @Path("/regioes")
    public Response getRegioes() {
        return Response.ok(hotelService.listarRegioes()).build();
    }

    @GET
    @Path("/estados/{regiaoId}")
    public Response getEstadosByRegiao(@PathParam("regiaoId") Long regiaoId) {
        return Response.ok(hotelService.listarEstadosPorRegiao(regiaoId)).build();
    }

    @GET
    @Path("/cidades/{estadoId}")
    public Response getCidadesByEstado(@PathParam("estadoId") Long estadoId) {
        return Response.ok(hotelService.listarCidadesPorEstado(estadoId)).build();
    }

    @GET
    @Path("/cidades")
    public Response listarCidades() {
        return Response.ok(hotelService.pegarTodasCidades()).build();
    }

    @GET
    @Path("/regiao/{regiao}")
    public Response pegarCidadadesPorRegiao(@PathParam("regiao") String regiao) {
        return Response.ok(hotelService.pegarCidadePorRegiao(regiao)).build();
    }

    @GET
    @Path("/cidade/regiao/{regiao}")
    public Response pegarHotelPorRegiao(@PathParam("regiao") String regiao) {
        return Response.ok(hotelService.pegarHotelPorRegiao(regiao)).build();
    }

    @POST
    public Response registrarHotel(HotelRegistroDTO hotel) {
        return Response.ok(hotelService.criarHotel(hotel)).build();
    }

    @PUT
    @Path("/{id}")
    public Response atualizarHotel(@PathParam("id") long id, HotelRegistroDTO hotel) {
        return Response.ok(hotelService.atualizarHotel(id, hotel)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deletarHotel(@PathParam("id") long id) {
        return Response.ok(hotelService.deletarHotel(id)).build();
    }
}
