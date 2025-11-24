package com.destino.projeto_destino.services.pacote;

import com.destino.projeto_destino.dto.dashboard.ViagensResponseDTO;
import com.destino.projeto_destino.dto.pacote.PacoteRegistroDTO;
import com.destino.projeto_destino.model.pacote.Pacote;
import com.destino.projeto_destino.model.pacote.hotel.Hotel;
import com.destino.projeto_destino.model.pacote.transporte.Transporte;
import com.destino.projeto_destino.model.usuario.Usuario;
import com.destino.projeto_destino.repository.HotelRepository;
import com.destino.projeto_destino.repository.PacoteRepository;
import com.destino.projeto_destino.repository.TransporteRepository;
import com.destino.projeto_destino.repository.UsuarioRepository;
import com.destino.projeto_destino.util.pacote.Status;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PacoteService {

    private final PacoteRepository pacoteRepository;
    private final HotelRepository hotelRepository;
    private final TransporteRepository transporteRepository;
    private final UsuarioRepository usuarioRepository;

    public PacoteService(PacoteRepository pacoteRepository, HotelRepository hotelRepository, TransporteRepository transporteRepository, UsuarioRepository usuarioRepository, HotelRepository hotelRepository1) {
        this.pacoteRepository = pacoteRepository;
        this.transporteRepository = transporteRepository;
        this.usuarioRepository = usuarioRepository;
        this.hotelRepository = hotelRepository1;
    }

    @Transactional
    public ResponseEntity<String> criarPacote(PacoteRegistroDTO pacoteRegistroDTO) {

        Hotel hotel = hotelRepository.findById(pacoteRegistroDTO.hotel())
                .orElseThrow(() -> new RuntimeException("Hotel não encontrado"));

        Transporte transporte = transporteRepository.findById(pacoteRegistroDTO.transporte())
                .orElseThrow(() -> new RuntimeException("Transporte não encontrado"));

        Usuario funcionario = usuarioRepository.findById(pacoteRegistroDTO.funcionario())
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        long diasViagem = ChronoUnit.DAYS.between(pacoteRegistroDTO.inicio(), pacoteRegistroDTO.fim());

        BigDecimal precoMinimo = new BigDecimal(
                BigInteger.valueOf(hotel.getDiaria() * diasViagem + transporte.getPreco()), 2
        );

        System.out.println("Preço Mínimo Calculado: " + precoMinimo);

        if (pacoteRegistroDTO.preco().compareTo(precoMinimo) < 0) {
            return ResponseEntity.badRequest().body("Preço do pacote não cobre os custos!");
        }

        Pacote pacote = Pacote.builder()
                .nome(pacoteRegistroDTO.nome())
                .funcionario(funcionario)
                .hotel(hotel)
                .transporte(transporte)
                .descricao(pacoteRegistroDTO.descricao())
                .tags(pacoteRegistroDTO.tags())
                .preco(pacoteRegistroDTO.preco())
                .inicio(pacoteRegistroDTO.inicio())
                .fim(pacoteRegistroDTO.fim())
                .disponibilidade(pacoteRegistroDTO.disponibilidade())
                .status(Status.EMANDAMENTO)
                .build();

        pacote = pacoteRepository.save(pacote);

        return ResponseEntity.ok().body("Pacote criado com sucesso!");
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
