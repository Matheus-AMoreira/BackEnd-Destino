package org.tech6.services.pacote;

import org.tech6.dto.pacote.PacoteRegistroDTO;
import org.tech6.dto.pacote.PacoteResponseDTO;
import org.tech6.model.pacote.Pacote;
import org.tech6.model.pacote.hotel.Hotel;
import org.tech6.model.pacote.pacoteFoto.PacoteFoto;
import org.tech6.model.pacote.transporte.Transporte;
import org.tech6.model.usuario.Usuario;
import org.tech6.repository.pacote.PacoteFotoRepository;
import org.tech6.repository.pacote.PacoteRepository;
import org.tech6.repository.pacote.TransporteRepository;
import org.tech6.repository.pacote.hotel.HotelRepository;
import org.tech6.repository.pacote.tag.TagRepository;
import org.tech6.repository.usuario.UsuarioRepository;
import org.tech6.model.pacote.tag.Tag;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class PacoteService {

    private final PacoteRepository pacoteRepository;
    private final HotelRepository hotelRepository;
    private final TransporteRepository transporteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PacoteFotoRepository pacoteFotoRepository;
    private final TagRepository tagRepository;

    public PacoteService(PacoteRepository pacoteRepository, HotelRepository hotelRepository,
            TransporteRepository transporteRepository, UsuarioRepository usuarioRepository,
            PacoteFotoRepository pacoteFotoRepository, TagRepository tagRepository) {
        this.pacoteRepository = pacoteRepository;
        this.hotelRepository = hotelRepository;
        this.transporteRepository = transporteRepository;
        this.usuarioRepository = usuarioRepository;
        this.pacoteFotoRepository = pacoteFotoRepository;
        this.tagRepository = tagRepository;
    }

    public Map<String, List<Pacote>> pegarPacotesAgrupadosPorLocal() {
        List<Pacote> todosPacotes = pacoteRepository.listAll();

        return todosPacotes.stream()
                .collect(Collectors.groupingBy(p -> {
                    return p.hotel.cidade.nome + " - " + p.hotel.cidade.estado.sigla;
                }));
    }

    // Retorna pacotes com paginação
    public List<PacoteResponseDTO> pegarPacotes(int page, int size) {
        return pacoteRepository.encontrePacotes()
                .page(page, size)
                .stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PacoteResponseDTO> buscarPacotesComFiltros(String nome, BigDecimal precoMax, int page, int size) {
        // Se a string vier vazia, passamos null para a query ignorar o filtro
        String termo = (nome != null && !nome.isBlank()) ? nome : null;

        return pacoteRepository.buscarComFiltros(termo, precoMax)
                .page(page, size)
                .stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    private PacoteResponseDTO converterParaResponseDTO(Pacote pacote) {
        return new PacoteResponseDTO(
                pacote.id,
                pacote.nome,
                pacote.descricao,
                pacote.tags.stream().toList(),
                pacote.ofertas,
                pacote.hotel,
                pacote.transporte,
                pacote.fotosDoPacote);
    }

    public Pacote pegarPacotePorNomeExato(String nome) {
        return pacoteRepository.findByNome(nome).orElse(null);
    }

    // Retorna os destinos mais vendidos (Top N)
    public List<PacoteResponseDTO> pacotesMaisvendidos() {
        return pacoteRepository.procuraPacotesMaisVendidos().stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public String criarPacote(PacoteRegistroDTO pacoteRegistroDTO) {
        // false indica que é um novo registro
        return salvarOuAtualizarPacote(new Pacote(), pacoteRegistroDTO, false);
    }

    @Transactional
    public String atualizarPacote(long id, PacoteRegistroDTO dto) {
        // Busca o existente, se achar, atualiza (true indica update)
        Pacote pacoteExistente = pacoteRepository.findById(id);
        if (pacoteExistente == null) {
            return null; // Or throw an exception
        }
        return salvarOuAtualizarPacote(pacoteExistente, dto, true);
    }

    private String salvarOuAtualizarPacote(Pacote pacote, PacoteRegistroDTO dto, boolean isUpdate) {
        Hotel hotel = hotelRepository.findById(dto.hotel());
        if (hotel == null) {
            throw new RuntimeException("Hotel não encontrado");
        }

        Transporte transporte = transporteRepository.findById(dto.transporte());
        if (transporte == null) {
            throw new RuntimeException("Transporte não encontrado");
        }

        Usuario funcionario = usuarioRepository.findById(dto.funcionario());
        if (funcionario == null) {
            throw new RuntimeException("Funcionário não encontrado");
        }

        PacoteFoto pacoteFoto = pacoteFotoRepository.findById(dto.pacoteFoto());

        // Preço e datas agora são gerenciados via Oferta.
        // O Pacote atua como o cabeçalho/base para várias ofertas.

        // Atualiza os dados do objeto
        pacote.nome = dto.nome();
        pacote.funcionario = funcionario;
        pacote.hotel = hotel;
        pacote.transporte = transporte;
        pacote.fotosDoPacote = pacoteFoto;
        pacote.descricao = dto.descricao();

        // Processamento de Tags
        Set<Tag> tags = new HashSet<>();
        if (dto.tags() != null) {
            for (String tagNome : dto.tags()) {
                Tag tag = tagRepository.findByNome(tagNome)
                        .orElseGet(() -> {
                            Tag novaTag = new Tag(tagNome);
                            tagRepository.persist(novaTag);
                            return novaTag;
                        });
                tags.add(tag);
            }
        }
        pacote.tags = tags;

        if (isUpdate) {
            // No need for explicit update with Panache in Transactional if the entity is
            // managed
        } else {
            pacoteRepository.persist(pacote);
        }

        return isUpdate ? "Pacote atualizado com sucesso!" : "Pacote criado com sucesso!";
    }

    public Pacote pegarPacotePorId(long id) {
        return pacoteRepository.findById(id);
    }

    public List<PacoteResponseDTO> pegarPacotesPorNome(String nome) {
        return pacoteRepository.procurePeloNome(nome).stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }
}