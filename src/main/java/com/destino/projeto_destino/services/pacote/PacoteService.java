package com.destino.projeto_destino.services.pacote;

import com.destino.projeto_destino.dto.dashboard.ViagensResponseDTO;
import com.destino.projeto_destino.dto.pacote.PacoteRegistroDTO;
import com.destino.projeto_destino.dto.pacote.TopDestinoDTO;
import com.destino.projeto_destino.model.pacote.Pacote;
import com.destino.projeto_destino.model.pacote.hotel.Hotel;
import com.destino.projeto_destino.model.pacote.pacoteFoto.PacoteFoto;
import com.destino.projeto_destino.model.pacote.transporte.Transporte;
import com.destino.projeto_destino.model.usuario.Usuario;
import com.destino.projeto_destino.repository.pacote.PacoteFotoRepository;
import com.destino.projeto_destino.repository.pacote.PacoteRepository;
import com.destino.projeto_destino.repository.pacote.TransporteRepository;
import com.destino.projeto_destino.repository.pacote.hotel.HotelRepository;
import com.destino.projeto_destino.repository.usuario.UsuarioRepository;
import com.destino.projeto_destino.util.model.pacote.Status;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PacoteService {

    private final PacoteRepository pacoteRepository;
    private final HotelRepository hotelRepository;
    private final TransporteRepository transporteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PacoteFotoRepository pacoteFotoRepository;

    public ResponseEntity<Map<String, List<Pacote>>> pegarPacotesAgrupadosPorLocal() {
        List<Pacote> todosPacotes = pacoteRepository.findAll();

        Map<String, List<Pacote>> agrupado = todosPacotes.stream()
                .collect(Collectors.groupingBy(p -> {
                    if (p.getHotel() != null && p.getHotel().getCidade() != null) {
                        return p.getHotel().getCidade().getNome() + " - " + p.getHotel().getCidade().getEstado().getSigla();
                    }
                    return "Destino Indefinido";
                }));

        return ResponseEntity.ok(agrupado);
    }

    public ResponseEntity<List<Pacote>> pegarPacotesPublicos() {
        return ResponseEntity.ok(pacoteRepository.findPacotesPublicos());
    }

    // Retorna pacotes com paginação
    public ResponseEntity<Page<Pacote>> pegarPacotesPaginados(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(pacoteRepository.findAll(pageable));
    }

    // Retorna os destinos mais vendidos (Top N)
    public ResponseEntity<List<TopDestinoDTO>> pegarDestinosMaisVendidos(int limite) {
        Pageable pageable = PageRequest.of(0, limite);
        return ResponseEntity.ok(pacoteRepository.findDestinosMaisVendidos(pageable));
    }

    @Transactional
    public ResponseEntity<String> criarPacote(PacoteRegistroDTO pacoteRegistroDTO) {
        // false indica que é um novo registro
        return salvarOuAtualizarPacote(new Pacote(), pacoteRegistroDTO, false);
    }

    // Lógica compartilhada para evitar duplicidade de código
    private ResponseEntity<String> salvarOuAtualizarPacote(Pacote pacote, PacoteRegistroDTO dto, boolean isUpdate) {
        Hotel hotel = hotelRepository.findById(dto.hotel())
                .orElseThrow(() -> new RuntimeException("Hotel não encontrado"));

        Transporte transporte = transporteRepository.findById(dto.transporte())
                .orElseThrow(() -> new RuntimeException("Transporte não encontrado"));

        Usuario funcionario = usuarioRepository.findById(dto.funcionario())
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        PacoteFoto pacoteFoto = pacoteFotoRepository.findById(dto.pacoteFoto()).orElse(null);

        long diasViagem = ChronoUnit.DAYS.between(dto.inicio(), dto.fim());

        BigDecimal valorDiaria = BigDecimal.valueOf(hotel.getDiaria());
        BigDecimal dias = BigDecimal.valueOf(diasViagem);
        BigDecimal valorTransporte = BigDecimal.valueOf(transporte.getPreco());

        // Regra de Negócio: Preço deve cobrir os custos
        BigDecimal precoMinimo = valorDiaria.multiply(dias).add(valorTransporte);

        if (dto.preco().compareTo(precoMinimo) < 0) {
            return ResponseEntity.badRequest().body("Preço do pacote não cobre os custos! Mínimo: " + precoMinimo);
        }

        // Atualiza os dados do objeto
        pacote.setNome(dto.nome());
        pacote.setFuncionario(funcionario);
        pacote.setHotel(hotel);
        pacote.setTransporte(transporte);
        pacote.setFotosDoPacote(pacoteFoto);
        pacote.setDescricao(dto.descricao());
        pacote.setTags(dto.tags());
        pacote.setPreco(dto.preco());
        pacote.setInicio(dto.inicio());
        pacote.setFim(dto.fim());
        pacote.setDisponibilidade(dto.disponibilidade());

        // Se for criação, define status inicial. Na edição, mantemos o status que já estava.
        if (!isUpdate) {
            pacote.setStatus(Status.EMANDAMENTO);
        }

        pacoteRepository.save(pacote);

        return ResponseEntity.ok().body(isUpdate ? "Pacote atualizado com sucesso!" : "Pacote criado com sucesso!");
    }

    @Transactional
    public ResponseEntity<String> atualizarPacote(int id, PacoteRegistroDTO dto) {
        // Busca o existente, se achar, atualiza (true indica update)
        return pacoteRepository.findById(id)
                .map(pacoteExistente -> salvarOuAtualizarPacote(pacoteExistente, dto, true))
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<Pacote> pegarPacotePorId(int id) {
        return pacoteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<List<Pacote>> pegarPacotes() {
        return ResponseEntity.ok().body(pacoteRepository.findAll());
    }

    public List<ViagensResponseDTO> pegarViagens() {
        return pacoteRepository.buscarViagens();
    }

    public ResponseEntity<List<Pacote>> pegarPacotesPorNome(String nome) {
        return ResponseEntity.ok().body(pacoteRepository.findByNome(nome));
    }
}