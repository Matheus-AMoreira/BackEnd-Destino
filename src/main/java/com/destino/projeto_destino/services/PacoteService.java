package com.destino.projeto_destino.services;

import com.destino.projeto_destino.dto.hotel.HotelRegistroDTO;
import com.destino.projeto_destino.dto.transporte.TransporteRegistroDTO;
import com.destino.projeto_destino.model.Hotel;
import com.destino.projeto_destino.model.Transporte;
import com.destino.projeto_destino.repository.HotelRepository;
import com.destino.projeto_destino.repository.PacoteRepository;
import com.destino.projeto_destino.repository.TransporteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacoteService {

    private final PacoteRepository pacoteRepository;
    private final HotelRepository hotelRepository;
    private final TransporteRepository transporteRepository;

    public PacoteService(
            PacoteRepository pacoteRepository,
            HotelRepository hotelRepository,
            TransporteRepository transporteRepository
    ) {
        this.pacoteRepository = pacoteRepository;
        this.hotelRepository = hotelRepository;
        this.transporteRepository = transporteRepository;
    }

    public ResponseEntity<String> criarTransportes(TransporteRegistroDTO transporteRegistroDTO){
        Transporte transporte = new Transporte();
        transporte.setEmpresa(transporteRegistroDTO.empresa());
        transporte.setMeio(transporteRegistroDTO.meio());
        transporte.setPreco(transporteRegistroDTO.preco());

        Transporte transporteCriado = transporteRepository.save(transporte);
        return ResponseEntity.ok().body("Transporte por meio " + transporteCriado.getMeio() + " em nome da empresa " + transporteCriado.getEmpresa() + " criado!");
    }

    public ResponseEntity<List<Transporte>> pegarTransportes(){
        List<Transporte> transporteList = transporteRepository.findAll();
        return ResponseEntity.ok().body(transporteList);
    }

    public ResponseEntity<String> criarHotel(HotelRegistroDTO hotelRegistroDTO){
        Hotel hotel = new Hotel();
        hotel.setNome(hotelRegistroDTO.nome());
        hotel.setEndereco(hotelRegistroDTO.endereco());
        hotel.setDiaria(hotelRegistroDTO.diaria());
        hotel.setCidade(hotelRegistroDTO.cidade());

        Hotel hotelCriado=hotelRepository.save(hotel);
        return ResponseEntity.ok().body("Hotel " + hotelCriado.getNome() + " criado!");
    }

    public ResponseEntity<List<Hotel>> pegarHoteis(){
        List<Hotel> hoteis=hotelRepository.findAll();
        return ResponseEntity.ok().body(hoteis);
    }
}
