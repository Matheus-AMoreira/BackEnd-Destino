package com.destino.projeto_destino.config;

import com.destino.projeto_destino.dto.pacote.local.IbgeCidadeDTO;
import com.destino.projeto_destino.dto.pacote.local.IbgeEstadoDTO;
import com.destino.projeto_destino.dto.pacote.local.IbgeRegiaoDTO;
import com.destino.projeto_destino.model.Avaliacao;
import com.destino.projeto_destino.model.Compra;
import com.destino.projeto_destino.model.pacote.Pacote;
import com.destino.projeto_destino.model.pacote.hotel.Hotel;
import com.destino.projeto_destino.model.pacote.hotel.cidade.Cidade;
import com.destino.projeto_destino.model.pacote.hotel.cidade.estado.Estado;
import com.destino.projeto_destino.model.pacote.hotel.cidade.estado.regiao.Regiao;
import com.destino.projeto_destino.model.pacote.transporte.Transporte;
import com.destino.projeto_destino.model.usuario.Usuario;
import com.destino.projeto_destino.repository.pacote.PacoteRepository;
import com.destino.projeto_destino.repository.pacote.TransporteRepository;
import com.destino.projeto_destino.repository.pacote.hotel.HotelRepository;
import com.destino.projeto_destino.repository.pacote.hotel.local.CidadeRepository;
import com.destino.projeto_destino.repository.pacote.hotel.local.EstadoRepository;
import com.destino.projeto_destino.repository.pacote.hotel.local.RegiaoRepository;
import com.destino.projeto_destino.repository.usuario.AvaliacaoRepository;
import com.destino.projeto_destino.repository.usuario.CompraRepository;
import com.destino.projeto_destino.repository.usuario.UsuarioRepository;
import com.destino.projeto_destino.util.model.compra.Metodo;
import com.destino.projeto_destino.util.model.compra.Processador;
import com.destino.projeto_destino.util.model.compra.StatusCompra;
import com.destino.projeto_destino.util.model.pacote.PacoteStatus;
import com.destino.projeto_destino.util.model.transporte.Meio;
import com.destino.projeto_destino.util.model.usuario.Cpf.Cpf;
import com.destino.projeto_destino.util.model.usuario.Telefone.Telefone;
import com.destino.projeto_destino.util.model.usuario.perfil.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private static final String IBGE_REGIOES_URL = "https://servicodados.ibge.gov.br/api/v1/localidades/regioes";
    private static final String IBGE_ESTADOS_URL = "https://servicodados.ibge.gov.br/api/v1/localidades/estados";
    private static final String IBGE_CIDADES_URL_TEMPLATE = "https://servicodados.ibge.gov.br/api/v1/localidades/estados/%s/municipios";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RegiaoRepository regiaoRepository;
    private final EstadoRepository estadoRepository;
    private final CidadeRepository cidadeRepository;
    private final RestTemplate restTemplate;
    private final HotelRepository hotelRepository;
    private final TransporteRepository transporteRepository;
    private final PacoteRepository pacoteRepository;
    private final CompraRepository compraRepository;
    private final AvaliacaoRepository avaliacaoRepository;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder,
                           RegiaoRepository regiaoRepository,
                           EstadoRepository estadoRepository,
                           CidadeRepository cidadeRepository, HotelRepository hotelRepository, TransporteRepository transporteRepository, PacoteRepository pacoteRepository, CompraRepository compraRepository, AvaliacaoRepository avaliacaoRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.regiaoRepository = regiaoRepository;
        this.estadoRepository = estadoRepository;
        this.cidadeRepository = cidadeRepository;
        this.hotelRepository = hotelRepository;
        this.transporteRepository = transporteRepository;
        this.pacoteRepository = pacoteRepository;
        this.compraRepository = compraRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.restTemplate = new RestTemplate();
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Iniciando verificação de dados iniciais...");

        //Criar usuários
        inserirUsuarios();

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

        // Criar Pacotes
        criarPacotesFicticios(gerarTransporteAleatorio());

        criarComprasFicticias();

        logger.info("Verificação de dados iniciais concluída.");
    }

    private void inserirUsuarios(){
        // Administrador
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

        // Funcionário
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

        criarUsuarioInvalidoAleatorio();
    }

    private void criarUsuarioInvalidoAleatorio() {
        logger.info("Criando um usuário inválido para testes...");
        Random random = new Random();

        String[] nomes = {"Marcos", "Joana", "Lucas", "Fernanda", "Gabriel", "Mariana", "Pedro", "Lara", "Bruno", "Carla"};
        String[] sobrenomes = {"Silva", "Santos", "Oliveira", "Souza", "Lima", "Pereira", "Ferreira", "Costa", "Almeida", "Nascimento"};

        String nome = nomes[random.nextInt(nomes.length)];
        String sobrenome = sobrenomes[random.nextInt(sobrenomes.length)];

        // --- LÓGICA DE E-MAIL REALISTA ---
        String[] separadores = {"_", ".", "-"};
        String separador = separadores[random.nextInt(separadores.length)];
        int quatroDigitos = 1000 + random.nextInt(9000); // Garante números entre 1000 e 9999

        // Exemplo: marcos.silva4821@destino.com
        String email = nome.toLowerCase() + separador + sobrenome.toLowerCase() + quatroDigitos + "@destino.com";
        // ---------------------------------

        String cpf = gerarCpfValido();

        // Telefone com DDD 11 a 99
        String ddd = String.valueOf(11 + random.nextInt(89));
        String telefone = ddd + "9" + (10000000 + random.nextInt(90000000));

        String senha = passwordEncoder.encode("123456");

        try {
            Usuario usuario = new Usuario(
                    nome,
                    sobrenome,
                    cpf,
                    email,
                    telefone,
                    senha,
                    UserRole.USUARIO,
                    false // Define como INVÁLIDO
            );

            usuarioRepository.save(usuario);
            logger.info("Usuário inválido criado: {} | CPF: {}", email, cpf);
        } catch (Exception e) {
            logger.error("Erro ao criar usuário inválido: {}", e.getMessage());
        }
    }

    private String gerarCpfValido() {
        Random random = new Random();
        int[] n = new int[9];

        // Gera os 9 primeiros dígitos
        for (int i = 0; i < 9; i++) {
            n[i] = random.nextInt(10);
        }

        // Calcula 1º dígito verificador
        int d1 = 0;
        for (int i = 0; i < 9; i++) {
            d1 += n[i] * (10 - i);
        }
        d1 = 11 - (d1 % 11);
        if (d1 >= 10) d1 = 0;

        // Calcula 2º dígito verificador
        int d2 = 0;
        for (int i = 0; i < 9; i++) {
            d2 += n[i] * (11 - i);
        }
        d2 += d1 * 2;
        d2 = 11 - (d2 % 11);
        if (d2 >= 10) d2 = 0;

        // Retorna formatado apenas com números (a classe Cpf lida com isso)
        return String.format("%d%d%d%d%d%d%d%d%d%d%d",
                n[0], n[1], n[2], n[3], n[4], n[5], n[6], n[7], n[8], d1, d2);
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
                continue;
            }

            List<Cidade> cidades = Arrays.stream(cidadesDTO)
                    .map(dto -> new Cidade(dto.id(), dto.nome(), estado))
                    .toList();

            cidadeRepository.saveAll(cidades);
            logger.debug("Cidades de {} carregadas.", estado.getSigla());
        }
    }

    private void criarUsuarioSeNaoExistir(String nome, String sobreNome, String cpf, String email,
                                          String telefone, String senhaPlana,
                                          UserRole perfil, boolean valido) {
        try {

            if (usuarioRepository.findByEmail(email).isEmpty()) {

                Cpf cpfObj = new Cpf(cpf);
                Telefone telObj = new Telefone(telefone);

                String senhaHasheada = passwordEncoder.encode(senhaPlana);

                Usuario novoUsuario = new Usuario(
                        nome,
                        sobreNome,
                        cpfObj.getValorPuro(),
                        email,
                        telObj.getValorPuro(),
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

    // Gerar transporte
    private Transporte gerarTransporteAleatorio() {
        String[] prefixos = {"Via", "Expresso", "Rápido", "Trans", "Viação", "Aero"};
        String[] sufixos = {"Brasil", "Norte", "Sul", "Log", "Tur", "Lines", "Way"};
        Random random = new Random();

        String nomeEmpresa = prefixos[random.nextInt(prefixos.length)] + " " +
                sufixos[random.nextInt(sufixos.length)];

        // Sorteia um meio de transporte (assumindo que Meio é um Enum)
        Meio[] meios = Meio.values();
        Meio meioSorteado = meios[random.nextInt(meios.length)];

        int preco = 100 + random.nextInt(900); // Preço entre 100 e 1000

        Transporte transporte = new Transporte(nomeEmpresa, meioSorteado, preco);
        return transporteRepository.save(transporte);
    }

    // Gerar hoteis
    private Hotel gerarHotelAleatorio(Cidade cidade) {
        String[] prefixos = {"Hotel", "Pousada", "Resort", "Hostel", "Palace", "Grand Hotel"};
        String[] nomes = {"Paradiso", "Central", "Vista Mar", "Imperial", "Sossego", "Horizonte", "Jardim", "Real"};
        Random random = new Random();

        String nomeHotel = prefixos[random.nextInt(prefixos.length)] + " " +
                nomes[random.nextInt(nomes.length)];

        String endereco = "Rua " + (random.nextInt(50) + 1) + ", Centro";
        int diaria = 80 + random.nextInt(420); // Diária entre 80 e 500

        return new Hotel(nomeHotel, endereco, diaria, cidade);
    }

    // Buscar cidades
    private Cidade buscarCidadeAleatoriaSemCarregarTudo() {
        long qtdeCidades = cidadeRepository.count();
        if (qtdeCidades == 0) return null;

        int indexAleatorio = ThreadLocalRandom.current().nextInt((int) qtdeCidades);
        Page<Cidade> cidadePage = cidadeRepository.findAll(PageRequest.of(indexAleatorio, 1));

        if (cidadePage.hasContent()) {
            return cidadePage.getContent().getFirst();
        }
        return null;
    }

    private Pacote gerarPacoteAleatorio(Hotel hotel, Transporte transporte, Usuario funcionario) {
        Random random = new Random();

        // --- DATA: De 2020-01-01 até (Hoje + 1 ano) ---
        LocalDate dataMinima = LocalDate.of(2020, 1, 1);
        LocalDate dataMaxima = LocalDate.now().plusYears(1);

        long diasTotaisRange = java.time.temporal.ChronoUnit.DAYS.between(dataMinima, dataMaxima);
        long diasAleatorios = random.nextLong(diasTotaisRange);

        LocalDate inicio = dataMinima.plusDays(diasAleatorios);
        LocalDate fim = inicio.plusDays(3 + random.nextInt(12)); // Duração de 3 a 15 dias
        // ---------------------------------------------

        // Nome do Pacote
        String[] adjetivos = {"Inesquecível", "Mágico", "Relaxante", "Aventura", "Romântico", "Luxuoso", "Econômico", "Histórico", "Exclusivo"};
        String nomePacote = adjetivos[random.nextInt(adjetivos.length)] + " em " + hotel.getCidade().getNome();

        // Tags
        String[] opcoesTags = {
                "Café da manhã incluso", "Piscina", "Wi-Fi Grátis", "Vista para o mar",
                "Pet Friendly", "All Inclusive", "Passeio de Barco", "City Tour",
                "Academia", "Spa", "Translado Grátis", "Ar Condicionado",
                "Estacionamento", "Kids Club", "Jantar Romântico", "Guia Turístico"
        };

        ArrayList<String> tags = new ArrayList<>();
        int qtdTags = 3 + random.nextInt(3);

        while (tags.size() < qtdTags) {
            String tagSorteada = opcoesTags[random.nextInt(opcoesTags.length)];
            if (!tags.contains(tagSorteada)) {
                tags.add(tagSorteada);
            }
        }

        // Descrição
        String descricao = "Pacote especial com hospedagem no " + hotel.getNome() +
                ". Aproveite nossos benefícios como " + tags.getFirst() + " e muito mais.";

        // Preço
        long diasDuracao = java.time.temporal.ChronoUnit.DAYS.between(inicio, fim);
        BigDecimal precoTotal = BigDecimal.valueOf(transporte.getPreco())
                .add(BigDecimal.valueOf(hotel.getDiaria() * diasDuracao));

        // --- STATUS: Lógica de Passado/Futuro + Chance de Cancelamento ---
        PacoteStatus status;

        // 1% de chance de ser CANCELADO (Sorteia 0 a 99, se for 0 cancela)
        if (random.nextInt(100) == 0) {
            status = PacoteStatus.CANCELADO;
        } else {
            // Se não cancelado, verifica data
            if (fim.isBefore(LocalDate.now())) {
                status = PacoteStatus.CONCLUIDO; // Já acabou
            } else {
                status = PacoteStatus.EMANDAMENTO; // Futuro ou acontecendo agora
            }
        }
        // ---------------------------------------------------------------

        return Pacote.builder()
                .nome(nomePacote)
                .descricao(descricao)
                .tags(tags)
                .preco(precoTotal)
                .inicio(inicio)
                .fim(fim)
                .disponibilidade(10 + random.nextInt(40))
                .status(status)
                .transporte(transporte)
                .hotel(hotel)
                .funcionario(funcionario)
                .fotosDoPacote(null)
                .build();
    }

    private void criarPacotesFicticios(Transporte transporte) {
        logger.info("Iniciando geração de pacotes e dados relacionados...");

        Usuario funcionario = usuarioRepository.findByEmail("funcionario@destino.com")
                .orElse(usuarioRepository.findAll().stream().findFirst().orElse(null));

        // BUSCA ÚNICA: Carrega usuários na memória 1 vez para usar em todos os pacotes
        List<Usuario> todosUsuarios = usuarioRepository.findAll();

        if (funcionario == null || todosUsuarios.isEmpty()) {
            logger.warn("Dados insuficientes (funcionário ou usuários) para gerar pacotes completos.");
            return;
        }

        // Loop de criação
        for (int i = 0; i < 5; i++) {
            Cidade cidadeAleatoria = buscarCidadeAleatoriaSemCarregarTudo();

            if (cidadeAleatoria != null) {
                Hotel hotel = gerarHotelAleatorio(cidadeAleatoria);
                hotelRepository.save(hotel);

                Pacote pacote = gerarPacoteAleatorio(hotel, transporte, funcionario);
                pacoteRepository.save(pacote); // Salva o Pacote primeiro

                // CHAMA O GERADOR AQUI: Passa o pacote recém-criado e a lista de usuários
                gerarAvaliacoesParaPacote(pacote, todosUsuarios);
            }
        }
        logger.info("Geração de pacotes, hotéis e avaliações concluída.");
    }

    private void criarComprasFicticias() {
        logger.info("Gerando histórico de compras fictícias...");

        List<Usuario> usuarios = usuarioRepository.findAll();
        List<Pacote> pacotes = pacoteRepository.findAll();

        if (usuarios.isEmpty() || pacotes.isEmpty()) {
            logger.warn("Não há usuários ou pacotes suficientes para gerar compras.");
            return;
        }

        Random random = new Random();
        int totalCompras = 0;

        // Tenta gerar compras para a maioria dos usuários
        for (Usuario usuario : usuarios) {
            // 30% de chance do usuário NÃO ter comprado nada
            if (random.nextInt(100) < 30) continue;

            // Usuário pode ter feito entre 1 e 3 viagens
            int qtdViagens = 1 + random.nextInt(3);

            for (int i = 0; i < qtdViagens; i++) {
                Pacote pacote = pacotes.get(random.nextInt(pacotes.size()));

                // Pula se o pacote estiver esgotado
                if (pacote.getDisponibilidade() <= 0) continue;

                Compra compra = new Compra();
                compra.setUsuario(usuario);
                compra.setPacote(pacote);

                // Data da compra: entre 1 e 60 dias atrás
                long diasAtras = 1 + random.nextInt(60);
                Date dataCompra = new Date(System.currentTimeMillis() - (diasAtras * 24 * 60 * 60 * 1000));
                compra.setDataCompra(dataCompra);

                // Decide Processador (40% PIX, 60% Cartão)
                boolean isPix = random.nextInt(100) < 40;

                if (isPix) {
                    compra.setProcessadorPagamento(Processador.PIX);
                    compra.setMetodo(Metodo.VISTA);
                    compra.setParcelas(1);
                    // Aplica 5% de desconto
                    compra.setValorFinal(pacote.getPreco().multiply(BigDecimal.valueOf(0.95)));
                } else {
                    // Decide bandeira do cartão
                    compra.setProcessadorPagamento(random.nextBoolean() ? Processador.VISA : Processador.MASTERCARD);

                    // Decide parcelas (1 a 12)
                    int parcelas = 1 + random.nextInt(12);
                    compra.setParcelas(parcelas);
                    compra.setMetodo(parcelas == 1 ? Metodo.VISTA : Metodo.PARCELADO);
                    compra.setValorFinal(pacote.getPreco());
                }

                compra.setStatus(StatusCompra.ACEITO);

                // Salva e atualiza estoque
                compraRepository.save(compra);
                pacote.setDisponibilidade(pacote.getDisponibilidade() - 1);
                pacoteRepository.save(pacote);
                totalCompras++;
            }
        }
        logger.info("Total de {} compras fictícias geradas.", totalCompras);
    }

    /**
     * Gera avaliações apenas para o pacote específico que acabou de ser criado.
     * Recebe a lista de usuários para evitar ir ao banco repetidamente.
     */
    private void gerarAvaliacoesParaPacote(Pacote pacote, List<Usuario> usuariosDisponiveis) {
        // Regra de Ouro: Só avalia se a viagem já acabou
        if (pacote.getFim().isAfter(LocalDate.now())) {
            return;
        }

        Random random = new Random();

        // Define quantos usuários avaliarão este pacote (entre 1 e 10, limitado ao total disponível)
        int maxUsers = Math.min(10, usuariosDisponiveis.size());
        if (maxUsers == 0) return;

        int qtdAvaliacoes = 1 + random.nextInt(maxUsers);

        // Embaralha uma cópia da lista para sortear usuários aleatórios sem alterar a original
        List<Usuario> sorteados = new ArrayList<>(usuariosDisponiveis);
        java.util.Collections.shuffle(sorteados);
        sorteados = sorteados.subList(0, qtdAvaliacoes);

        for (Usuario usuario : sorteados) {
            // 1. Garante a Compra (Venda Forçada Retroativa)
            // Verifica se já existe compra APENAS se necessário (otimização)
            boolean jaComprou = compraRepository.findByUsuarioAndPacote(usuario, pacote).isPresent();

            if (!jaComprou) {
                Compra compra = new Compra();
                compra.setUsuario(usuario);
                compra.setPacote(pacote);
                // Data da compra: 15 a 45 dias antes do início
                compra.setDataCompra(java.sql.Date.valueOf(pacote.getInicio().minusDays(15 + random.nextInt(30))));
                compra.setMetodo(Metodo.VISTA);
                compra.setProcessadorPagamento(Processador.PIX);
                compra.setParcelas(1);
                compra.setValorFinal(pacote.getPreco());
                compra.setStatus(StatusCompra.ACEITO);
                compraRepository.save(compra);
            }

            // 2. Verifica se já avaliou (Prevenção de Duplicidade)
            if (avaliacaoRepository.findByUsuarioAndPacote(usuario, pacote).isPresent()) {
                continue;
            }

            // 3. Gera Conteúdo da Avaliação
            Avaliacao avaliacao = criarAvaliacaoAleatoria(usuario, pacote, random);
            avaliacaoRepository.save(avaliacao);
        }
        logger.debug("Geradas {} avaliações para o pacote: {}", qtdAvaliacoes, pacote.getNome());
    }

    // Metodo auxiliar para limpar o código principal e centralizar os textos
    private Avaliacao criarAvaliacaoAleatoria(Usuario usuario, Pacote pacote, Random random) {
        String[] positivos = {
                "A viagem foi incrível, superou as expectativas!", "Lugar maravilhoso.",
                "Tudo perfeito, do hotel ao transporte.", "Experiência única.", "Serviço impecável."
        };
        String[] neutros = {
                "Foi bom, mas esperava mais.", "O hotel era bom, o transporte atrasou.",
                "Razoável pelo preço.", "Valeu a experiência."
        };
        String[] negativos = {
                "Não gostei da organização.", "Limpeza deixou a desejar.",
                "Problemas com o transporte.", "Muito caro para o que oferece."
        };

        int nota;
        String comentario;
        int chance = random.nextInt(100);

        if (chance < 70) { // 70% Positivo
            nota = 4 + random.nextInt(2);
            comentario = positivos[random.nextInt(positivos.length)];
        } else if (chance < 90) { // 20% Neutro
            nota = 3;
            comentario = neutros[random.nextInt(neutros.length)];
        } else { // 10% Negativo
            nota = 1 + random.nextInt(2);
            comentario = negativos[random.nextInt(negativos.length)];
        }

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setUsuario(usuario);
        avaliacao.setPacote(pacote);
        avaliacao.setNota(nota);
        avaliacao.setComentario(comentario);
        // Data: entre 1 e 5 dias após o fim da viagem
        avaliacao.setData(java.sql.Date.valueOf(pacote.getFim().plusDays(1 + random.nextInt(5))));

        return avaliacao;
    }
}
