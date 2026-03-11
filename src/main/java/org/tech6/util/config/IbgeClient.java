package org.tech6.util.config;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.tech6.dto.pacote.local.IbgeCidadeDTO;
import org.tech6.dto.pacote.local.IbgeEstadoDTO;
import org.tech6.dto.pacote.local.IbgeRegiaoDTO;

import java.util.List;

@RegisterRestClient(baseUri = "https://servicodados.ibge.gov.br/api/v1")
public interface IbgeClient {

    @GET
    @Path("/localidades/regioes")
    List<IbgeRegiaoDTO> getRegioes();

    @GET
    @Path("/localidades/regioes/{regiaoId}/estados")
    List<IbgeEstadoDTO> getEstadosPorRegiao(@PathParam("regiaoId") Long regiaoId);

    @GET
    @Path("/localidades/estados/{uf}/municipios")
    List<IbgeCidadeDTO> getCidadesPorEstado(@PathParam("uf") String uf);
}
