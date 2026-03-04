package com.fatec.destino.config;

import com.fatec.destino.dto.pacote.local.IbgeCidadeDTO;
import com.fatec.destino.dto.pacote.local.IbgeEstadoDTO;
import com.fatec.destino.dto.pacote.local.IbgeRegiaoDTO;
import com.fatec.destino.model.pacote.hotel.cidade.Cidade;
import com.fatec.destino.model.pacote.hotel.cidade.estado.Estado;
import com.fatec.destino.model.pacote.hotel.cidade.estado.regiao.Regiao;
import com.fatec.destino.repository.pacote.hotel.local.CidadeRepository;
import com.fatec.destino.repository.pacote.hotel.local.EstadoRepository;
import com.fatec.destino.repository.pacote.hotel.local.RegiaoRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    @Value("${ibge.regioes.url}")
    private String IBGE_REGIOES_URL;

    @Value("${ibge.estados.url}")
    private String IBGE_ESTADOS_URL;

    @Value("${ibge.cidades.url.template}")
    private String IBGE_CIDADES_URL_TEMPLATE;

    private final RestTemplate restTemplate = new RestTemplate();

    private final RegiaoRepository regiaoRepository;
    private final EstadoRepository estadoRepository;
    private final CidadeRepository cidadeRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Vereficando dados das regiões do brasil");

        if (cidadeRepository.count() > 0) {
            log.info(
                    "Dados de localização (Regiões, Estados, Cidades) já estão carregados."
            );
        } else {
            log.info("Iniciando a carga de dados de localização do IBGE...");
            try {
                Map<Long, Regiao> mapaRegioes = carregarRegioes();
                List<Estado> estados = carregarEstados(mapaRegioes);
                carregarCidades(estados);
                log.info(
                        "Carga de dados de localização completa. Total de {} cidades carregadas.",
                        cidadeRepository.count()
                );
            } catch (Exception e) {
                log.error(
                        "Erro catastrófico ao carregar dados do IBGE. A transação será revertida.",
                        e
                );
            }
        }

        log.info("Verificação de dados iniciais concluída.");
    }

    private Map<Long, Regiao> carregarRegioes() {
        log.debug("Buscando Regiões...");
        IbgeRegiaoDTO[] regioesDTO = restTemplate.getForObject(
                IBGE_REGIOES_URL,
                IbgeRegiaoDTO[].class
        );

        if (regioesDTO == null) {
            throw new RuntimeException(
                    "Falha ao buscar regiões do IBGE. Resposta nula."
            );
        }

        List<Regiao> regioes = Arrays.stream(regioesDTO)
                .map(dto -> new Regiao(dto.id(), dto.sigla(), dto.nome()))
                .toList();

        regiaoRepository.saveAll(regioes);
        log.info("Regiões carregadas com sucesso.");

        return regioes
                .stream()
                .collect(Collectors.toMap(Regiao::getId, Function.identity()));
    }

    private List<Estado> carregarEstados(Map<Long, Regiao> mapaRegioes) {
        log.debug("Buscando Estados...");
        IbgeEstadoDTO[] estadosDTO = restTemplate.getForObject(
                IBGE_ESTADOS_URL,
                IbgeEstadoDTO[].class
        );

        if (estadosDTO == null) {
            throw new RuntimeException(
                    "Falha ao buscar estados do IBGE. Resposta nula."
            );
        }

        List<Estado> estados = Arrays.stream(estadosDTO)
                .map(dto -> {
                    Regiao regiao = mapaRegioes.get(dto.regiao().id());
                    if (regiao == null) {
                        throw new RuntimeException(
                                "Inconsistência de dados: Região ID " +
                                        dto.regiao().id() +
                                        " não encontrada para o estado " +
                                        dto.sigla()
                        );
                    }
                    return new Estado(dto.id(), dto.sigla(), dto.nome(), regiao);
                })
                .toList();

        estadoRepository.saveAll(estados);
        log.info("Estados carregados com sucesso.");
        return estados;
    }

    private void carregarCidades(List<Estado> estados) {
        log.debug("Buscando Cidades (municípios)...");

        for (Estado estado : estados) {
            String urlCidades = String.format(
                    IBGE_CIDADES_URL_TEMPLATE,
                    estado.getSigla()
            );

            IbgeCidadeDTO[] cidadesDTO = restTemplate.getForObject(
                    urlCidades,
                    IbgeCidadeDTO[].class
            );
            if (cidadesDTO == null) {
                log.warn(
                        "Falha ao buscar cidades para o estado {}. Pulando.",
                        estado.getSigla()
                );
                continue;
            }

            List<Cidade> cidades = Arrays.stream(cidadesDTO)
                    .map(dto -> new Cidade(dto.id(), dto.nome(), estado))
                    .toList();

            cidadeRepository.saveAll(cidades);
            log.debug("Cidades de {} carregadas.", estado.getSigla());
        }
    }
}
