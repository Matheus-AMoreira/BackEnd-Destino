package com.destino.projeto_destino.services.pacote;

import com.destino.projeto_destino.dto.pacote.hotel.HotelRegistroDTO;
import com.destino.projeto_destino.model.pacote.hotel.Hotel;
import com.destino.projeto_destino.model.pacote.hotel.cidade.Cidade;
import com.destino.projeto_destino.repository.local.CidadeRepository;
import com.destino.projeto_destino.repository.pacote.HotelRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;
    private final CidadeRepository cidadeRepository;

    public HotelService(HotelRepository hotelRepository, CidadeRepository cidadeRepository) {
        this.hotelRepository = hotelRepository;
        this.cidadeRepository = cidadeRepository;
    }

    @Transactional
    public ResponseEntity<String> criarHotel(HotelRegistroDTO hotelRegistroDTO) {
        Cidade cidadeReferencia = cidadeRepository.getReferenceById(
                hotelRegistroDTO.cidade()
        );
        Hotel hotelCriado = hotelRepository.save(new Hotel(hotelRegistroDTO, cidadeReferencia));
        return ResponseEntity.ok().body("Hotel " + hotelCriado.getNome() + " criado!");
    }

    public ResponseEntity<List<Hotel>> pegarHoteis() {
        List<Hotel> hoteis = hotelRepository.findAll();
        return ResponseEntity.ok().body(hoteis);
    }

    public ResponseEntity<List<Hotel>> pegarHotelPorRegiao(String regiao) {
        return ResponseEntity.ok().body(hotelRepository.findByCidadeEstadoRegiaoNome(regiao));
    }

    public ResponseEntity<List<Cidade>> pegarCidadePorRegiao(String regiao) {
        return ResponseEntity.ok().body(cidadeRepository.findByEstadoRegiaoNome(regiao));
    }
}
