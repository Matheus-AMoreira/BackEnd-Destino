package com.fatec.destino.config

import com.fatec.destino.dto.pacote.local.IbgeCidadeDTO
import com.fatec.destino.dto.pacote.local.IbgeEstadoDTO
import com.fatec.destino.dto.pacote.local.IbgeRegiaoDTO
import com.fatec.destino.model.Avaliacao
import com.fatec.destino.model.Compra
import com.fatec.destino.model.pacote.Pacote
import com.fatec.destino.model.pacote.hotel.Hotel
import com.fatec.destino.model.pacote.hotel.cidade.Cidade
import com.fatec.destino.model.pacote.hotel.cidade.estado.Estado
import com.fatec.destino.model.pacote.hotel.cidade.estado.regiao.Regiao
import com.fatec.destino.model.pacote.transporte.Transporte
import com.fatec.destino.model.usuario.Usuario
import com.fatec.destino.repository.pacote.PacoteRepository
import com.fatec.destino.repository.pacote.TransporteRepository
import com.fatec.destino.repository.pacote.hotel.HotelRepository
import com.fatec.destino.repository.pacote.hotel.local.CidadeRepository
import com.fatec.destino.repository.pacote.hotel.local.EstadoRepository
import com.fatec.destino.repository.pacote.hotel.local.RegiaoRepository
import com.fatec.destino.repository.usuario.AvaliacaoRepository
import com.fatec.destino.repository.usuario.CompraRepository
import com.fatec.destino.repository.usuario.UsuarioRepository
import com.fatec.destino.util.model.compra.Metodo
import com.fatec.destino.util.model.compra.Processador
import com.fatec.destino.util.model.compra.StatusCompra
import com.fatec.destino.util.model.pacote.PacoteStatus
import com.fatec.destino.util.model.transporte.Meio
import com.fatec.destino.util.model.usuario.cpf.Cpf
import com.fatec.destino.util.model.usuario.perfil.UserRole
import com.fatec.destino.util.model.usuario.telefone.Telefone
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import java.sql.Date
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Random
import java.util.concurrent.ThreadLocalRandom

@Component
class DataInitializer(
    private val usuarioRepository: UsuarioRepository,
    private val passwordEncoder: PasswordEncoder,
    private val regiaoRepository: RegiaoRepository,
    private val estadoRepository: EstadoRepository,
    private val cidadeRepository: CidadeRepository,
    private val hotelRepository: HotelRepository,
    private val transporteRepository: TransporteRepository,
    private val pacoteRepository: PacoteRepository,
    private val compraRepository: CompraRepository,
    private val avaliacaoRepository: AvaliacaoRepository
) {

    private val logger = LoggerFactory.getLogger(DataInitializer::class.java)
    private val restTemplate = RestTemplate()

    companion object {
        private const val IBGE_REGIOES_URL = "https://servicodados.ibge.gov.br/api/v1/localidades/regioes"
        private const val IBGE_ESTADOS_URL = "https://servicodados.ibge.gov.br/api/v1/localidades/estados"
        private const val IBGE_CIDADES_URL_TEMPLATE = "https://servicodados.ibge.gov.br/api/v1/localidades/estados/%s/municipios"
    }

    @Transactional
    fun carregar(args: String) {
        logger.info("Iniciando verificação de dados iniciais...")

        if (localizacaoJaCarregada()) {
            logger.info("Dados de localização (Regiões, Estados, Cidades) já estão carregados.")
        } else {
            logger.info("Iniciando a carga de dados de localização do IBGE...")
            runCatching {
                val mapaRegioes = carregarRegioes()
                val estados = carregarEstados(mapaRegioes)
                carregarCidades(estados)
                logger.info("Carga de dados de localização completa. Total de {} cidades carregadas.", cidadeRepository.count())
            }.onFailure { e ->
                logger.error("Erro catastrófico ao carregar dados do IBGE. A transação será revertida.", e)
            }
        }

        if(args.contains("seed")){
            println("Gerando dados e inserindo no banco")
            inserirUsuarios(5)
            criarPacotesFicticios(100, gerarTransporteAleatorio())
        }

        logger.info("Verificação de dados iniciais concluída.")
    }

    private fun inserirUsuarios(vezes: Int) {
        criarUsuarioSeNaoExistir("Aministrador", "Silva", "16506961082", "administrador@destino.com", "4438858699", "dJGu83561SUWP!", UserRole.ADMINISTRADOR, true)
        criarUsuarioSeNaoExistir("Funcionário", "Silva", "71827029080", "funcionario@destino.com", "44981811400", "FuNc!2834984", UserRole.FUNCIONARIO, true)

        repeat(vezes) { criarUsuarioInvalidoAleatorio() }
    }

    private fun criarUsuarioInvalidoAleatorio() {
        logger.info("Criando um usuário inválido para testes...")
        val random = Random()

        val nomes = arrayOf("Marcos", "Joana", "Lucas", "Fernanda", "Gabriel", "Mariana", "Pedro", "Lara", "Bruno", "Carla")
        val sobrenomes = arrayOf("Silva", "Santos", "Oliveira", "Souza", "Lima", "Pereira", "Ferreira", "Costa", "Almeida", "Nascimento")

        val nome = nomes.random()
        val sobrenome = sobrenomes.random()
        val separador = arrayOf("_", ".", "-").random()
        val quatroDigitos = 1000 + random.nextInt(9000)

        // Gerando os dados brutos
        val emailStr = "${nome.lowercase()}$separador${sobrenome.lowercase()}$quatroDigitos@destino.com"
        val telefoneStr = "${(11..99).random()}9${10000000 + random.nextInt(90000000)}"
        val senhaHasheada = passwordEncoder.encode("123456").toString()

        runCatching {
            val novoUsuario = Usuario(
                nome = nome,
                sobreNome = sobrenome,
                cpf = gerarCpfValido(),
                email = emailStr,
                telefone = Telefone(telefoneStr),
                senha = senhaHasheada,
                perfil = UserRole.USUARIO,
                valido = false
            )

            val usuarioSalvo = usuarioRepository.save(novoUsuario)
            logger.info("Usuário inválido criado: ${usuarioSalvo.email} | CPF: ${usuarioSalvo.cpf}")
        }.onFailure { e ->
            logger.error("Erro ao criar usuário inválido: ${e.message}")
        }
    }

    private fun gerarCpfValido(): Cpf {
        val random = Random()
        val n = IntArray(9) { random.nextInt(10) }

        fun calcularDigito(base: IntArray, pesoInicial: Int): Int {
            var soma = 0
            base.forEachIndexed { i, d -> soma += d * (pesoInicial - i) }
            val resto = soma % 11
            return if (resto < 2) 0 else 11 - resto
        }

        val d1 = calcularDigito(n, 10)
        val d2 = calcularDigito(n + d1, 11)

        val cpfString = n.joinToString("") + d1.toString() + d2.toString()

        return Cpf(cpfString)
    }

    private fun localizacaoJaCarregada() = cidadeRepository.count() > 0

    private fun carregarRegioes(): Map<Long, Regiao> {
        logger.debug("Buscando Regiões...")
        val regioesDTO = restTemplate.getForObject(IBGE_REGIOES_URL, Array<IbgeRegiaoDTO>::class.java)
            ?: throw RuntimeException("Falha ao buscar regiões do IBGE. Resposta nula.")

        val regioes = regioesDTO.map { Regiao(it.id, it.sigla, it.nome) }
        regiaoRepository.saveAll(regioes)

        return regioes.associateBy { it.id as Long }
    }

    private fun carregarEstados(mapaRegioes: Map<Long, Regiao>): List<Estado> {
        val estadosDTO = restTemplate.getForObject(IBGE_ESTADOS_URL, Array<IbgeEstadoDTO>::class.java)
            ?: throw RuntimeException("Falha ao buscar estados do IBGE. Resposta nula.")

        val estados = estadosDTO.map { dto ->
            val regiao = mapaRegioes[dto.regiao().id()]
                ?: throw RuntimeException("Inconsistência: Região ${dto.regiao().id()} não encontrada para o estado ${dto.sigla()}")
            Estado(dto.id, dto.sigla, dto.nome, regiao)
        }

        estadoRepository.saveAll(estados)
        return estados
    }

    private fun carregarCidades(estados: List<Estado>) {
        estados.forEach { estado ->
            val url = IBGE_CIDADES_URL_TEMPLATE.format(estado.sigla)
            val cidadesDTO = restTemplate.getForObject(url, Array<IbgeCidadeDTO>::class.java)

            if (cidadesDTO != null) {
                val cidades = cidadesDTO.map { Cidade(it.id(), it.nome(), estado) }
                cidadeRepository.saveAll(cidades)
                logger.debug("Cidades de ${estado.sigla} carregadas.")
            }
        }
    }

    private fun criarUsuarioSeNaoExistir(nome: String, sobreNome: String, cpf: String, email: String,
                                         telefone: String, senhaPlana: String, perfil: UserRole, valido: Boolean) {
        runCatching {
            if (usuarioRepository.findByEmail(email).isEmpty) {
                val telObj = Telefone(telefone)
                val senhaHasheada = passwordEncoder.encode(senhaPlana).toString()

                val novoUsuario =
                    Usuario(
                        nome,
                        sobreNome,
                        gerarCpfValido(),
                        email,
                        telObj,
                        senhaHasheada,
                        perfil,
                        valido
                    )
                usuarioRepository.save(novoUsuario)
                logger.info("Usuário criado: $email")
            } else {
                logger.warn("Usuário já existe: $email")
            }
        }.onFailure { e -> logger.error("Falha ao criar usuário $email: ${e.message}") }
    }

    private fun gerarTransporteAleatorio(): Transporte {
        val nomeEmpresa = "${arrayOf("Via", "Expresso", "Rápido", "Viação", "Aero").random()} ${arrayOf("Brasil", "Norte", "Sul", "Log", "Tur", "Lines", "Way").random()}"
        val meioSorteado = Meio.entries.toTypedArray().random()
        val preco = 100 + Random().nextInt(900)

        return transporteRepository.save(Transporte(nomeEmpresa, meioSorteado, preco))
    }

    private fun gerarHotelAleatorio(cidade: Cidade): Hotel {
        val nomeHotel = "${arrayOf("Hotel", "Pousada", "Resort", "Hostel", "Palace", "Grand Hotel").random()} ${arrayOf("Paradiso", "Central", "Vista Mar", "Imperial", "Sossego", "Horizonte", "Jardim", "Real").random()}"
        val diaria = 80 + Random().nextInt(420)
        return Hotel(nomeHotel, "Rua ${Random().nextInt(50) + 1}, Centro", diaria, cidade)
    }

    private fun buscarCidadeAleatoriaSemCarregarTudo(): Cidade? {
        val qtde = cidadeRepository.count()
        if (qtde == 0L) return null
        val index = ThreadLocalRandom.current().nextInt(qtde.toInt())
        return cidadeRepository.findAll(PageRequest.of(index, 1)).content.firstOrNull()
    }

    private fun gerarPacoteAleatorio(hotel: Hotel, transporte: Transporte, funcionario: Usuario): Pacote {
        val random = Random()
        val dataMinima = LocalDate.of(2020, 1, 1)
        val diasRange = ChronoUnit.DAYS.between(dataMinima, LocalDate.now().plusYears(1))
        val inicio = dataMinima.plusDays(random.nextLong(diasRange))
        val fim = inicio.plusDays(3L + random.nextInt(12))

        val tags = mutableSetOf<String>().apply {
            val opcoes = arrayOf("Café da manhã incluso", "Piscina", "Wi-Fi Grátis", "Vista para o mar", "Pet Friendly", "All Inclusive")
            while (size < 3) add(opcoes.random())
        }.toCollection(ArrayList<String?>())

        val precoTotal = BigDecimal.valueOf(transporte.preco.toLong() + hotel.diaria.toLong() * 10)

        val status = when {
            random.nextInt(100) == 0 -> PacoteStatus.CANCELADO
            fim.isBefore(LocalDate.now()) -> PacoteStatus.CONCLUIDO
            else -> PacoteStatus.EMANDAMENTO
        }

        return Pacote(
                nome = "${arrayOf("Inesquecível", "Mágico", "Aventura").random()} em ${hotel.cidade?.nome}",
                descricao = "Pacote no ${hotel.nome}. Benefícios: ${tags.toString()}",
                tags = tags,
                preco = precoTotal,
                inicio = inicio,
                fim = fim,
                disponibilidade = 10 + random.nextInt(40),
                status = status,
                transporte = transporte,
                hotel = hotel,
                funcionario = funcionario
            )
    }

    private fun criarPacotesFicticios(vezes: Int, transporte: Transporte) {
        val funcionario = usuarioRepository.findByEmail("funcionario@destino.com").orElseGet {
            usuarioRepository.findAll().firstOrNull()
        } ?: return

        val todosUsuarios = usuarioRepository.findAll()

        repeat(vezes) {
            buscarCidadeAleatoriaSemCarregarTudo()?.let { cidade ->
                val hotel = hotelRepository.save(gerarHotelAleatorio(cidade))
                val pacote = pacoteRepository.save(gerarPacoteAleatorio(hotel, transporte, funcionario))
                gerarAvaliacoesParaPacote(pacote, todosUsuarios)
            }
        }
    }

    private fun gerarAvaliacoesParaPacote(pacote: Pacote, usuarios: List<Usuario>) {
        if (pacote.fim?.isAfter(LocalDate.now()) ?: return) return

        val random = Random()
        val sorteados = usuarios.shuffled().take(1 + random.nextInt(minOf(10, usuarios.size)))

        sorteados.forEach { usuario ->
            if (compraRepository.findByUsuarioAndPacote(usuario, pacote).isEmpty) {
                compraRepository.save(Compra(
                    usuario = usuario,
                    pacote = pacote,
                    dataCompra = Date.valueOf(pacote.inicio?.minusDays(15L + random.nextInt(30))),
                    metodo = Metodo.VISTA,
                    processadorPagamento = Processador.PIX,
                    parcelas = 1,
                    valorFinal = pacote.preco,
                    statusCompra = StatusCompra.ACEITO
                ))
            }

            if (avaliacaoRepository.findByUsuarioAndPacote(usuario, pacote).isEmpty) {
                avaliacaoRepository.save(criarAvaliacaoAleatoria(usuario, pacote, random))
            }
        }
    }

    private fun criarAvaliacaoAleatoria(usuario: Usuario, pacote: Pacote, random: Random): Avaliacao {
        val chance = random.nextInt(100)
        val (nota, comentario) = when {
            chance < 70 -> (4..5).random() to "A viagem foi incrível!"
            chance < 90 -> 3 to "Foi bom, mas esperava mais."
            else -> (1..2).random() to "Não gostei da organização."
        }

        return Avaliacao(
            usuario = usuario,
            pacote = pacote,
            nota = nota,
            comentario = comentario,
            data = Date.valueOf(pacote.fim?.plusDays(1L + random.nextInt(5)))
        )
    }
}