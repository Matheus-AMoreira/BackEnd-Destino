package com.fatec.destino.config

import com.fatec.destino.dto.auth.registro.UsuarioRegistroDTO
import com.fatec.destino.dto.avaliacao.AvaliacaoRegistroDTO
import com.fatec.destino.dto.compra.CompraRequestDTO
import com.fatec.destino.dto.hotel.HotelRegistroDTO
import com.fatec.destino.dto.local.IbgeCidadeDTO
import com.fatec.destino.dto.local.IbgeEstadoDTO
import com.fatec.destino.dto.local.IbgeRegiaoDTO
import com.fatec.destino.dto.pacote.PacoteRegistroDTO
import com.fatec.destino.dto.pacoteFoto.PacoteFotoRegistroDTO
import com.fatec.destino.dto.transporte.TransporteRegistroDTO
import com.fatec.destino.model.pacote.hotel.cidade.Cidade
import com.fatec.destino.model.pacote.hotel.cidade.estado.Estado
import com.fatec.destino.model.pacote.hotel.cidade.estado.regiao.Regiao
import com.fatec.destino.model.usuario.Usuario
import com.fatec.destino.repository.pacote.pacoteFotoRepository.PacoteFotoRepository
import com.fatec.destino.repository.pacote.PacoteRepository
import com.fatec.destino.repository.pacote.hotel.HotelRepository
import com.fatec.destino.repository.pacote.hotel.local.CidadeRepository
import com.fatec.destino.repository.pacote.hotel.local.EstadoRepository
import com.fatec.destino.repository.pacote.hotel.local.RegiaoRepository
import com.fatec.destino.repository.pacote.transporte.TransporteRepository
import com.fatec.destino.repository.usuario.UsuarioRepository
import com.fatec.destino.services.auth.AuthenticationService
import com.fatec.destino.services.compra.CompraService
import com.fatec.destino.services.pacoteFoto.PacoteFotoService
import com.fatec.destino.services.pacote.PacoteService
import com.fatec.destino.services.transporte.TransporteService
import com.fatec.destino.services.hotel.HotelService
import com.fatec.destino.services.usuario.AvaliacaoService
import com.fatec.destino.util.model.compra.Metodo
import com.fatec.destino.util.model.compra.Processador
import com.fatec.destino.util.model.transporte.Meio
import com.fatec.destino.util.model.usuario.cpf.Cpf
import com.fatec.destino.util.model.usuario.perfil.UserRole
import com.fatec.destino.util.model.usuario.telefone.Telefone
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.time.LocalDate
import java.util.*
import java.util.concurrent.ThreadLocalRandom

@Component
class DataInitializer(
    // Services (Lógica de Negócio)
    private val authenticationService: AuthenticationService,
    private val transporteService: TransporteService,
    private val hotelService: HotelService,
    private val pacoteService: PacoteService,
    private val pacoteFotoService: PacoteFotoService,
    private val compraService: CompraService,
    private val avaliacaoService: AvaliacaoService,

    // Repositories (Necessários para recuperar IDs gerados e dados IBGE)
    private val usuarioRepository: UsuarioRepository,
    private val regiaoRepository: RegiaoRepository,
    private val estadoRepository: EstadoRepository,
    private val cidadeRepository: CidadeRepository,
    private val transporteRepository: TransporteRepository,
    private val hotelRepository: HotelRepository,
    private val pacoteFotoRepository: PacoteFotoRepository,
    private val pacoteRepository: PacoteRepository,
    private val passwordEncoder: PasswordEncoder
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

        if (args.contains("seed")) {
            println("Gerando dados e inserindo no banco via Services...")
            inserirUsuarios(5)
            val transporteId = criarTransporteAleatorioViaService()
            criarPacotesFicticiosViaService(100, transporteId)
        }

        logger.info("Verificação de dados iniciais concluída.")
    }

    private fun inserirUsuarios(vezes: Int) {
        criarUsuarioEspecialSeNaoExistir("Aministrador", "Silva", "16506961082", "administrador@destino.com", "4438858699", "dJGu83561SUWP!", UserRole.ADMINISTRADOR)
        criarUsuarioEspecialSeNaoExistir("Funcionário", "Silva", "71827029080", "funcionario@destino.com", "44981811400", "FuNc!2834984", UserRole.FUNCIONARIO)

        repeat(vezes) { criarUsuarioComumAleatorioViaService() }
    }

    private fun criarUsuarioComumAleatorioViaService() {
        logger.info("Criando um usuário comum via Service...")
        val random = Random()
        val nomes = arrayOf("Marcos", "Joana", "Lucas", "Fernanda", "Gabriel", "Mariana", "Pedro", "Lara", "Bruno", "Carla")
        val sobrenomes = arrayOf("Silva", "Santos", "Oliveira", "Souza", "Lima", "Pereira", "Ferreira", "Costa", "Almeida", "Nascimento")

        val nome = nomes.random()
        val sobrenome = sobrenomes.random()
        val quatroDigitos = 1000 + random.nextInt(9000)

        val emailStr = "${nome.lowercase()}.${sobrenome.lowercase()}$quatroDigitos@destino.com"
        val telefoneStr = "${(11..99).random()}9${10000000 + random.nextInt(90000000)}"

        // Senha forte para passar no validador
        val senhaForte = "Senha@${random.nextInt(1000)}"

        val dto = UsuarioRegistroDTO(
            nome = nome,
            sobreNome = sobrenome,
            cpf = gerarCpfValido(),
            email = emailStr,
            telefone = Telefone(telefoneStr),
            senha = senhaForte
        )

        val response = authenticationService.cadastrarUsuario(dto)
        if (response.erro) {
            logger.error("Erro ao cadastrar usuário ${dto.email}: ${response.mensagem}")
        } else {
            logger.info("Usuário cadastrado com sucesso via Service: ${dto.email}")
        }
    }

    private fun criarTransporteAleatorioViaService(): Long? {
        val nomeEmpresa = "${arrayOf("Via", "Expresso", "Rápido", "Viação", "Aero").random()} ${arrayOf("Brasil", "Norte", "Sul", "Log", "Tur", "Lines").random()} ${Random().nextInt(999)}"
        val meioSorteado = Meio.entries.toTypedArray().random()
        val preco = 100 + Random().nextInt(900)

        val dto = TransporteRegistroDTO(nomeEmpresa, meioSorteado, preco)

        transporteService.criarTransportes(dto)

        // Recupera o ID do transporte criado (assumindo ser o último ou buscando pelo nome único)
        // Como o service retorna String, buscamos no banco para continuar o fluxo
        return transporteRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).firstOrNull()?.id
    }

    private fun criarPacotesFicticiosViaService(vezes: Int, transporteId: Long?) {
        if (transporteId == null) {
            logger.error("Transporte não criado. Abortando criação de pacotes.")
            return
        }

        val funcionario = usuarioRepository.findByEmail("funcionario@destino.com")
            ?: usuarioRepository.findAll().firstOrNull { it.perfil == UserRole.FUNCIONARIO || it.perfil == UserRole.ADMINISTRADOR }

        if (funcionario == null) {
            logger.error("Funcionário não encontrado.")
            return
        }

        repeat(vezes) {
            buscarCidadeAleatoriaSemCarregarTudo()?.let { cidade ->
                // 1. Criar Hotel via Service
                val hotelId = criarHotelViaService(cidade)

                // 2. Criar PacoteFoto via Service (Opcional, criando dummy)
                val fotoId = criarPacoteFotoViaService()

                // 3. Criar Pacote via Service
                if (hotelId != null && fotoId != null) {
                    val pacoteId = criarPacoteViaService(hotelId, transporteId, fotoId, funcionario.id!!, cidade.nome)

                    // 4. Gerar Compras e Avaliações via Service
                    if (pacoteId != null) {
                        gerarInteracoesViaService(pacoteId)
                    }
                }
            }
        }
    }

    private fun criarHotelViaService(cidade: Cidade): Long? {
        val nomeHotel = "${arrayOf("Hotel", "Pousada", "Resort").random()} ${arrayOf("Paradiso", "Central", "Vista").random()} ${Random().nextInt(1000)}"
        val diaria = 80 + Random().nextInt(420)

        // Note: DTO espera ID da cidade
        val dto = HotelRegistroDTO(nomeHotel, "Rua Aleatoria, ${Random().nextInt(100)}", diaria, cidade.id)

        hotelService.criarHotel(dto)

        // Recupera ID
        return hotelRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).firstOrNull()?.id
    }

    private fun criarPacoteFotoViaService(): Long? {
        val dto = PacoteFotoRegistroDTO(
            "Fotos Genéricas",
            "https://placehold.co/600x400",
            null
        )
        pacoteFotoService.criarPacoteFoto(dto)
        return pacoteFotoRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).firstOrNull()?.id
    }

    private fun criarPacoteViaService(hotelId: Long, transporteId: Long, fotoId: Long, funcionarioId: UUID, nomeCidade: String): Long? {
        val random = Random()
        val dataMinima = LocalDate.now().minusDays(100)
        val inicio = dataMinima.plusDays(random.nextLong(200)) // Viagens passadas e futuras
        val fim = inicio.plusDays(3L + random.nextInt(12))

        val nomePacote = "${arrayOf("Inesquecível", "Mágico", "Aventura").random()} em $nomeCidade"
        val desc = "Pacote completo com hotel e transporte incluso."
        val tags = listOf("Café da manhã", "Wi-Fi", "Piscina")

        // Cálculo básico para não falhar na validação de preço do Service
        // Preço deve ser >= (diaria * dias) + transporte
        // Como não temos os valores exatos aqui fácil, jogamos um valor alto seguro
        val precoEstimado = 5000 + random.nextInt(2000)

        val dto = PacoteRegistroDTO(
            nome = nomePacote,
            descricao = desc,
            tags = tags,
            preco = precoEstimado,
            inicio = inicio,
            fim = fim,
            disponibilidade = 10 + random.nextInt(20),
            transporte = transporteId,
            hotel = hotelId,
            pacoteFoto = fotoId,
            funcionario = funcionarioId
        )

        try {
            pacoteService.salvarOuAtualizar(dto, null)
            return pacoteRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).firstOrNull()?.id
        } catch (e: Exception) {
            logger.error("Erro ao criar pacote via service: ${e.message}")
            return null
        }
    }

    private fun gerarInteracoesViaService(pacoteId: Long) {
        val pacote = pacoteRepository.findById(pacoteId).orElse(null) ?: return

        // Só avalia se a viagem já ocorreu (lógica simples)
        if (pacote.inicio.isAfter(LocalDate.now())) return

        val usuarios = usuarioRepository.findAll().filter { it.perfil == UserRole.USUARIO }
        if (usuarios.isEmpty()) return

        val random = Random()
        // Seleciona alguns usuários para comprar
        val compradores = usuarios.shuffled().take(1 + random.nextInt(3))

        compradores.forEach { usuario ->
            // 1. Compra via Service
            val compraDto = CompraRequestDTO(
                usuarioId = usuario.id!!,
                pacoteId = pacoteId,
                metodo = Metodo.VISTA,
                processador = Processador.PIX,
                parcelas = 1
            )

            try {
                compraService.processarCompra(compraDto)

                // 2. Avaliação via Service (se comprou)
                val nota = 1 + random.nextInt(5)
                val comentario = if (nota > 3) "Muito bom!" else "Pode melhorar."

                val avaliacaoDto = AvaliacaoRegistroDTO(
                    usuarioId = usuario.id!!,
                    pacoteId = pacoteId,
                    nota = nota,
                    comentario = comentario
                )
                avaliacaoService.avaliarPacote(avaliacaoDto)

            } catch (e: Exception) {
                logger.warn("Erro ao gerar interacao (compra/avaliacao): ${e.message}")
            }
        }
    }

    // --- Métodos Auxiliares e Legado (IBGE / Utils) ---

    private fun gerarCpfValido(): Cpf {
        val random = Random()
        val n = IntArray(9) { random.nextInt(10) }
        val d1 = calcularDigito(n, 10)
        val d2 = calcularDigito(n + d1, 11)
        return Cpf(n.joinToString("") + d1 + d2)
    }

    private fun calcularDigito(base: IntArray, pesoInicial: Int): Int {
        var soma = 0
        base.forEachIndexed { i, d -> soma += d * (pesoInicial - i) }
        val resto = soma % 11
        return if (resto < 2) 0 else 11 - resto
    }

    private fun criarUsuarioEspecialSeNaoExistir(nome: String, sobreNome: String, cpf: String, email: String, telefone: String, senhaPlana: String, perfil: UserRole) {
        if (usuarioRepository.findByEmail(email) == null) {
            val novoUsuario = Usuario(
                nome, sobreNome, Cpf(cpf), email, Telefone(telefone),
                passwordEncoder.encode(senhaPlana), perfil, true
            )
            usuarioRepository.save(novoUsuario)
        }
    }

    private fun localizacaoJaCarregada() = cidadeRepository.count() > 0

    private fun carregarRegioes(): Map<Long, Regiao> {
        val regioesDTO = restTemplate.getForObject(IBGE_REGIOES_URL, Array<IbgeRegiaoDTO>::class.java) ?: emptyArray()
        val regioes = regioesDTO.map { Regiao(it.id, it.sigla, it.nome) }
        regiaoRepository.saveAll(regioes)
        return regioes.associateBy { it.id as Long }
    }

    private fun carregarEstados(mapaRegioes: Map<Long, Regiao>): List<Estado> {
        val estadosDTO = restTemplate.getForObject(IBGE_ESTADOS_URL, Array<IbgeEstadoDTO>::class.java) ?: emptyArray()
        val estados = estadosDTO.map { dto -> Estado(dto.id, dto.sigla, dto.nome, mapaRegioes[dto.regiao.id]!!) }
        estadoRepository.saveAll(estados)
        return estados
    }

    private fun carregarCidades(estados: List<Estado>) {
        estados.forEach { estado ->
            val url = IBGE_CIDADES_URL_TEMPLATE.format(estado.sigla)
            val cidadesDTO = restTemplate.getForObject(url, Array<IbgeCidadeDTO>::class.java)
            if (cidadesDTO != null) {
                cidadeRepository.saveAll(cidadesDTO.map { Cidade(it.id, it.nome, estado) })
            }
        }
    }

    private fun buscarCidadeAleatoriaSemCarregarTudo(): Cidade? {
        val qtde = cidadeRepository.count()
        if (qtde == 0L) return null
        val index = ThreadLocalRandom.current().nextInt(qtde.toInt())
        return cidadeRepository.findAll(PageRequest.of(index, 1)).content.firstOrNull()
    }
}