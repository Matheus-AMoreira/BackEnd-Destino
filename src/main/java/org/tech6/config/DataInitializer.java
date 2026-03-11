package org.tech6.config;

import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.tech6.dto.pacote.local.IbgeCidadeDTO;
import org.tech6.dto.pacote.local.IbgeEstadoDTO;
import org.tech6.dto.pacote.local.IbgeRegiaoDTO;
import org.tech6.model.pacote.hotel.cidade.Cidade;
import org.tech6.model.pacote.hotel.cidade.estado.Estado;
import org.tech6.model.pacote.hotel.cidade.estado.regiao.Regiao;
import org.tech6.repository.pacote.hotel.local.CidadeRepository;
import org.tech6.repository.pacote.hotel.local.EstadoRepository;
import org.tech6.repository.pacote.hotel.local.RegiaoRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import org.tech6.util.config.IbgeClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class DataInitializer {

        private static final Logger log = Logger.getLogger(DataInitializer.class);

        @Inject
        @RestClient
        IbgeClient ibgeClient;

        @Inject
        RegiaoRepository regiaoRepository;
        @Inject
        EstadoRepository estadoRepository;
        @Inject
        CidadeRepository cidadeRepository;

        @Transactional
        public void onStart(@Observes StartupEvent ev) {
                if (cidadeRepository.count() > 0) {
                        log.info("Dados de localização já existem no banco.");
                        return;
                }

                log.info("Iniciando carga hierárquica do IBGE...");

                try {
                        // 1. Busca todas as Regiões
                        List<IbgeRegiaoDTO> regioesDTO = ibgeClient.getRegioes();

                        for (IbgeRegiaoDTO rDto : regioesDTO) {
                                Regiao regiao = new Regiao(rDto.id(), rDto.sigla(), rDto.nome());
                                regiaoRepository.persist(regiao);
                                log.infof("Processando Região: %s", regiao.nome);

                                // 2. Busca Estados apenas DESTA região
                                List<IbgeEstadoDTO> estadosDTO = ibgeClient.getEstadosPorRegiao(rDto.id());

                                for (IbgeEstadoDTO eDto : estadosDTO) {
                                        Estado estado = new Estado(eDto.id(), eDto.sigla(), eDto.nome(), regiao);
                                        estadoRepository.persist(estado);
                                        log.infof("  -> Estado: %s", estado.nome);

                                        // 3. Busca Cidades apenas DESTE estado
                                        List<IbgeCidadeDTO> cidadesDTO = ibgeClient.getCidadesPorEstado(eDto.sigla());

                                        List<Cidade> cidades = cidadesDTO.stream()
                                                .map(cDto -> new Cidade(cDto.id(), cDto.nome(), estado))
                                                .toList();

                                        cidadeRepository.persist(cidades);
                                }
                        }
                        log.info("Carga de dados concluída com sucesso!");

                } catch (Exception e) {
                        log.error("Erro ao carregar dados do IBGE: " + e.getMessage(), e);
                }
        }
}
