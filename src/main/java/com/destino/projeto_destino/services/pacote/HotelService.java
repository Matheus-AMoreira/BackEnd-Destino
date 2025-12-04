package com.destino.projeto_destino.services.pacote;

import com.destino.projeto_destino.dto.pacote.hotel.HotelRegistroDTO;
import com.destino.projeto_destino.model.pacote.hotel.Hotel;
import com.destino.projeto_destino.model.pacote.hotel.cidade.Cidade;
import com.destino.projeto_destino.model.pacote.hotel.cidade.estado.Estado;
import com.destino.projeto_destino.model.pacote.hotel.cidade.estado.regiao.Regiao;
import com.destino.projeto_destino.repository.pacote.PacoteRepository;
import com.destino.projeto_destino.repository.pacote.hotel.HotelRepository;
import com.destino.projeto_destino.repository.pacote.hotel.local.CidadeRepository;
import com.destino.projeto_destino.repository.pacote.hotel.local.EstadoRepository;
import com.destino.projeto_destino.repository.pacote.hotel.local.RegiaoRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

;

@Service
@AllArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final CidadeRepository cidadeRepository;
    private final EstadoRepository estadoRepository;
    private final RegiaoRepository regiaoRepository;
    private final PacoteRepository pacoteRepository;

    // --- Métodos Auxiliares de Localização (Cascata) ---

    public ResponseEntity<List<Regiao>> listarRegioes() {
        return ResponseEntity.ok(regiaoRepository.findAll());
    }

    public ResponseEntity<List<Estado>> listarEstadosPorRegiao(Long regiaoId) {
        return ResponseEntity.ok(estadoRepository.findByRegiaoId(regiaoId));
    }

    public ResponseEntity<List<Cidade>> listarCidadesPorEstado(Long estadoId) {
        return ResponseEntity.ok(cidadeRepository.findByEstadoId(estadoId));
    }

    public List<Hotel> pegarHoteis() {
        return hotelRepository.findAll();
    }

    public Hotel pegarHotelById(long id) {
        return hotelRepository.findHotelById(id);
    }

    // --- CRUD Hotel ---

    @Transactional
    public ResponseEntity<String> criarHotel(HotelRegistroDTO hotelRegistroDTO) {
        Cidade cidadeReferencia = cidadeRepository.getReferenceById(
                hotelRegistroDTO.cidade()
        );
        Hotel hotelCriado = hotelRepository.save(new Hotel(hotelRegistroDTO, cidadeReferencia));
        return ResponseEntity.ok().body("Hotel " + hotelCriado.getNome() + " criado!");
    }

    @Transactional
    public ResponseEntity<String> atualizarHotel(long id, HotelRegistroDTO dto) {
        return hotelRepository.findById(id).map(hotel -> {
            Cidade cidade = cidadeRepository.getReferenceById(dto.cidade());

            hotel.setNome(dto.nome());
            hotel.setEndereco(dto.endereco());
            hotel.setDiaria(dto.diaria());
            hotel.setCidade(cidade);

            hotelRepository.save(hotel);
            return ResponseEntity.ok("Hotel atualizado com sucesso!");
        }).orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<String> deletarHotel(long id) {
        if (!hotelRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Verifica se algum pacote usa este hotel
        if (pacoteRepository.existsByHotelId(id)) {
            return ResponseEntity.badRequest().body("Não é possível deletar: Existem pacotes vinculados a este Hotel.");
        }

        hotelRepository.deleteById(id);
        return ResponseEntity.ok("Hotel deletado com sucesso.");
    }

    public ResponseEntity<List<Cidade>> pegarTodasCidades() {
        return ResponseEntity.ok(cidadeRepository.findAll());
    }

    public ResponseEntity<List<Hotel>> pegarHotelPorRegiao(String regiao) {
        return ResponseEntity.ok().body(hotelRepository.findByCidadeEstadoRegiaoNome(regiao));
    }

    public ResponseEntity<List<Cidade>> pegarCidadePorRegiao(String regiao) {
        return ResponseEntity.ok().body(cidadeRepository.findByEstadoRegiaoNome(regiao));
    }
}
