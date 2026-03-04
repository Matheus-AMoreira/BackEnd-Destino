package com.fatec.destino.services.pacote;

import com.fatec.destino.dto.pacote.PacoteRegistroDTO;
import com.fatec.destino.dto.pacote.PacoteResponseDTO;
import com.fatec.destino.model.pacote.Pacote;
import com.fatec.destino.model.pacote.hotel.Hotel;
import com.fatec.destino.model.pacote.pacoteFoto.PacoteFoto;
import com.fatec.destino.model.pacote.transporte.Transporte;
import com.fatec.destino.model.usuario.Usuario;
import com.fatec.destino.repository.pacote.PacoteFotoRepository;
import com.fatec.destino.repository.pacote.PacoteRepository;
import com.fatec.destino.repository.pacote.TransporteRepository;
import com.fatec.destino.repository.pacote.hotel.HotelRepository;
import com.fatec.destino.repository.pacote.tag.TagRepository;
import com.fatec.destino.repository.usuario.UsuarioRepository;
import com.fatec.destino.model.pacote.tag.Tag;
import com.fatec.destino.util.model.pacote.OfertaStatus;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PacoteService {

    private final PacoteRepository pacoteRepository;
    private final HotelRepository hotelRepository;
    private final TransporteRepository transporteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PacoteFotoRepository pacoteFotoRepository;
    private final TagRepository tagRepository;

    public ResponseEntity<Map<String, List<Pacote>>> pegarPacotesAgrupadosPorLocal() {
        List<Pacote> todosPacotes = pacoteRepository.findAll();

        Map<String, List<Pacote>> agrupado = todosPacotes.stream()
                .collect(Collectors.groupingBy(p -> {
                    return p.getHotel().getCidade().getNome() + " - " + p.getHotel().getCidade().getEstado().getSigla();
                }));

        return ResponseEntity.ok(agrupado);
    }

    // Retorna pacotes com paginação
    public Page<PacoteResponseDTO> pegarPacotes(Pageable pageable) {
        return pacoteRepository.encontrePacotes(pageable).map(this::converterParaResponseDTO);
    }

    public Page<PacoteResponseDTO> buscarPacotesComFiltros(String nome, BigDecimal precoMax, Pageable pageable) {
        // Se a string vier vazia, passamos null para a query ignorar o filtro
        String termo = (nome != null && !nome.isBlank()) ? nome : null;

        return pacoteRepository.buscarComFiltros(termo, precoMax, pageable)
                .map(this::converterParaResponseDTO);
    }

    private PacoteResponseDTO converterParaResponseDTO(Pacote pacote) {
        return new PacoteResponseDTO(
                pacote.getId(),
                pacote.getNome(),
                pacote.getDescricao(),
                pacote.getTags().stream().toList(),
                pacote.getOfertas(),
                pacote.getHotel(),
                pacote.getTransporte(),
                pacote.getFotosDoPacote());
    }

    public ResponseEntity<Pacote> pegarPacotePorNomeExato(String nome) {
        return pacoteRepository.findByNome(nome)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Retorna os destinos mais vendidos (Top N)
    public List<PacoteResponseDTO> pacotesMaisvendidos() {
        return pacoteRepository.procuraPacotesMaisVendidos().stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ResponseEntity<String> criarPacote(PacoteRegistroDTO pacoteRegistroDTO) {
        // false indica que é um novo registro
        return salvarOuAtualizarPacote(new Pacote(), pacoteRegistroDTO, false);
    }

    @Transactional
    public ResponseEntity<String> atualizarPacote(long id, PacoteRegistroDTO dto) {
        // Busca o existente, se achar, atualiza (true indica update)
        return pacoteRepository.findById(id)
                .map(pacoteExistente -> salvarOuAtualizarPacote(pacoteExistente, dto, true))
                .orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<String> salvarOuAtualizarPacote(Pacote pacote, PacoteRegistroDTO dto, boolean isUpdate) {
        Hotel hotel = hotelRepository.findById(dto.hotel())
                .orElseThrow(() -> new RuntimeException("Hotel não encontrado"));

        Transporte transporte = transporteRepository.findById(dto.transporte())
                .orElseThrow(() -> new RuntimeException("Transporte não encontrado"));

        Usuario funcionario = usuarioRepository.findById(dto.funcionario())
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        PacoteFoto pacoteFoto = pacoteFotoRepository.findById(dto.pacoteFoto()).orElse(null);

        // Preço e datas agora são gerenciados via Oferta.
        // O Pacote atua como o cabeçalho/base para várias ofertas.

        // Atualiza os dados do objeto
        pacote.setNome(dto.nome());
        pacote.setFuncionario(funcionario);
        pacote.setHotel(hotel);
        pacote.setTransporte(transporte);
        pacote.setFotosDoPacote(pacoteFoto);
        pacote.setDescricao(dto.descricao());

        // Processamento de Tags
        Set<Tag> tags = new HashSet<>();
        if (dto.tags() != null) {
            for (String tagNome : dto.tags()) {
                Tag tag = tagRepository.findByNome(tagNome)
                        .orElseGet(() -> {
                            Tag novaTag = Tag.builder().nome(tagNome).build();
                            return tagRepository.save(novaTag);
                        });
                tags.add(tag);
            }
        }
        pacote.setTags(tags);

        pacoteRepository.save(pacote);

        return ResponseEntity.ok().body(isUpdate ? "Pacote atualizado com sucesso!" : "Pacote criado com sucesso!");
    }

    public ResponseEntity<Pacote> pegarPacotePorId(long id) {
        return pacoteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public List<PacoteResponseDTO> pegarPacotesPorNome(String nome) {
        return pacoteRepository.procurePeloNome(nome).stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }
}