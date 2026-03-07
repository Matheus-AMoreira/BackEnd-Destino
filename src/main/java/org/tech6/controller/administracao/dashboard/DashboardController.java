package org.tech6.controller.administracao.dashboard;

import org.tech6.services.dashboard.DashboardService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;

@Path("/api/dashboard")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"ROLE_FUNCIONARIO", "ROLE_ADMINISTRADOR"})
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GET
    @Path("/status-viagem")
    public Response getStatusViagem() {
        return Response.ok(dashboardService.getStatusDistribution()).build();
    }

    @GET
    @Path("/transporte-stats")
    public Response getTransporteStats() {
        return Response.ok(dashboardService.getTransporteDistribution()).build();
    }

    @GET
    @Path("/viagens/mensais")
    public Response getViagensMensais(@QueryParam("ano") Integer ano) {
        int anoFiltro = (ano != null) ? ano : LocalDate.now().getYear();
        return Response.ok(dashboardService.getViagensConcluidasPorMes(anoFiltro)).build();
    }

    @GET
    @Path("/viagens/vendidos")
    public Response getComprasMensais(@QueryParam("ano") Integer ano) {
        int anoFiltro = (ano != null) ? ano : LocalDate.now().getYear();
        return Response.ok(dashboardService.getComprasPorMes(anoFiltro)).build();
    }
}
