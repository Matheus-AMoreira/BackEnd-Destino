package com.destino.projeto_destino.config;

import com.destino.projeto_destino.dto.local.IbgeCidadeDTO;
import com.destino.projeto_destino.dto.local.IbgeEstadoDTO;
import com.destino.projeto_destino.dto.local.IbgeRegiaoDTO;
import com.destino.projeto_destino.model.Usuario;
import com.destino.projeto_destino.model.local.Cidade;
import com.destino.projeto_destino.model.local.Estado;
import com.destino.projeto_destino.model.local.Regiao;
import com.destino.projeto_destino.model.usuarioUtils.Cpf.Cpf;
import com.destino.projeto_destino.model.usuarioUtils.Email.Email;
import com.destino.projeto_destino.model.usuarioUtils.Telefone.Telefone;
import com.destino.projeto_destino.model.usuarioUtils.UserRole;
import com.destino.projeto_destino.repository.UserRepository;
import com.destino.projeto_destino.repository.local.CidadeRepository;
import com.destino.projeto_destino.repository.local.EstadoRepository;
import com.destino.projeto_destino.repository.local.RegiaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private static final String IBGE_REGIOES_URL = "https://servicodados.ibge.gov.br/api/v1/localidades/regioes";
    private static final String IBGE_ESTADOS_URL = "https://servicodados.ibge.gov.br/api/v1/localidades/estados";
    private static final String IBGE_CIDADES_URL_TEMPLATE = "https://servicodados.ibge.gov.br/api/v1/localidades/estados/%s/municipios";

    private final UserRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RegiaoRepository regiaoRepository;
    private final EstadoRepository estadoRepository;
    private final CidadeRepository cidadeRepository;
    private final RestTemplate restTemplate;

    public DataInitializer(
            UserRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            RegiaoRepository regiaoRepository,
            EstadoRepository estadoRepository,
            CidadeRepository cidadeRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.regiaoRepository = regiaoRepository;
        this.estadoRepository = estadoRepository;
        this.cidadeRepository = cidadeRepository;
        this.restTemplate = new RestTemplate();
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Iniciando verificação de dados iniciais...");

        // 1. Administrador
        criarUsuarioSeNaoExistir(
                "Aministrador",
                "Silva",
                "16506961082",
                "administrador@destino.com",
                "4438858699",
                "dJGu83561SUWP!",
                UserRole.ADMINISTRADOR,
                true
        );

        // 2. Funcionário
        criarUsuarioSeNaoExistir(
                "Funcionário",
                "Silva",
                "71827029080",
                "funcionario@destino.com",
                "44981811400",
                "FuNc!2834984",
                UserRole.FUNCIONARIO,
                true
        );

        // 3. Usuário
        criarUsuarioSeNaoExistir(
                "Usuário",
                "Silva",
                "25625316554",
                "usuario@destino.com",
                "44981811400",
                "Us-Rio3245!",
                UserRole.USUARIO,
                true
        );

        if (localizacaoJaCarregada()) {
            logger.info("Dados de localização (Regiões, Estados, Cidades) já estão carregados.");
        } else {
            logger.info("Iniciando a carga de dados de localização do IBGE...");
            try {
                Map<Long, Regiao> mapaRegioes = carregarRegioes();
                List<Estado> estados = carregarEstados(mapaRegioes);
                carregarCidades(estados);
                logger.info("Carga de dados de localização completa. Total de {} cidades carregadas.", cidadeRepository.count());
            } catch (Exception e) {
                logger.error("Erro catastrófico ao carregar dados do IBGE. A transação será revertida.", e);
            }
        }

        logger.info("Verificação de dados iniciais concluída.");
    }

    private void criarUsuarioSeNaoExistir(String nome, String sobreNome,String cpf, String email,
                                          String telefone, String senhaPlana,
                                          UserRole perfil, boolean valido) {
        try {
            Email emailObj = new Email(email);

            if (usuarioRepository.findByEmail(emailObj).isEmpty()) {

                Cpf cpfObj = new Cpf(cpf);
                Telefone telObj = new Telefone(telefone);

                String senhaHasheada = passwordEncoder.encode(senhaPlana);

                Usuario novoUsuario = new Usuario(
                        nome,
                        sobreNome,
                        cpfObj,
                        emailObj,
                        telObj,
                        senhaHasheada,
                        perfil,
                        valido
                );

                usuarioRepository.save(novoUsuario);
                logger.info("Usuário criado: {}", email);
            } else {
                logger.warn("Usuário já existe, não foi criado: {}", email);
            }
        } catch (Exception e) {
            logger.error("Falha ao criar usuário {}: {}", email, e.getMessage());
        }
    }

    private boolean localizacaoJaCarregada() {
        return cidadeRepository.count() > 0;
    }

    private Map<Long, Regiao> carregarRegioes() {
        logger.debug("Buscando Regiões...");
        IbgeRegiaoDTO[] regioesDTO = restTemplate.getForObject(IBGE_REGIOES_URL, IbgeRegiaoDTO[].class);

        if (regioesDTO == null) {
            throw new RuntimeException("Falha ao buscar regiões do IBGE. Resposta nula.");
        }

        List<Regiao> regioes = Arrays.stream(regioesDTO)
                .map(dto -> new Regiao(dto.id(), dto.sigla(), dto.nome()))
                .toList();

        regiaoRepository.saveAll(regioes);
        logger.info("Regiões carregadas com sucesso.");

        // Retorna um mapa para facilitar a associação com os estados
        return regioes.stream()
                .collect(Collectors.toMap(Regiao::getId, Function.identity()));
    }

    private List<Estado> carregarEstados(Map<Long, Regiao> mapaRegioes) {
        logger.debug("Buscando Estados...");
        IbgeEstadoDTO[] estadosDTO = restTemplate.getForObject(IBGE_ESTADOS_URL, IbgeEstadoDTO[].class);

        if (estadosDTO == null) {
            throw new RuntimeException("Falha ao buscar estados do IBGE. Resposta nula.");
        }

        List<Estado> estados = Arrays.stream(estadosDTO)
                .map(dto -> {
                    // Busca a região correspondente no mapa
                    Regiao regiao = mapaRegioes.get(dto.regiao().id());
                    if (regiao == null) {
                        throw new RuntimeException("Inconsistência de dados: Região ID " + dto.regiao().id() + " não encontrada para o estado " + dto.sigla());
                    }
                    return new Estado(dto.id(), dto.sigla(), dto.nome(), regiao);
                })
                .toList();

        estadoRepository.saveAll(estados);
        logger.info("Estados carregados com sucesso.");
        return estados; // Retorna a lista para a próxima etapa
    }

    private void carregarCidades(List<Estado> estados) {
        logger.debug("Buscando Cidades (municípios)...");

        for (Estado estado : estados) {
            String urlCidades = String.format(IBGE_CIDADES_URL_TEMPLATE, estado.getSigla());

            IbgeCidadeDTO[] cidadesDTO = restTemplate.getForObject(urlCidades, IbgeCidadeDTO[].class);
            if (cidadesDTO == null) {
                logger.warn("Falha ao buscar cidades para o estado {}. Pulando.", estado.getSigla());
                continue; // Não é catastrófico, apenas pula este estado
            }

            List<Cidade> cidades = Arrays.stream(cidadesDTO)
                    .map(dto -> new Cidade(dto.id(), dto.nome(), estado))
                    .toList();

            cidadeRepository.saveAll(cidades);
            logger.debug("Cidades de {} carregadas.", estado.getSigla());
        }
    }

}
