package org.tech6.services.pacote;

import org.tech6.dto.pacote.hotel.HotelRegistroDTO;
import org.tech6.model.pacote.hotel.Hotel;
import org.tech6.model.pacote.hotel.cidade.Cidade;
import org.tech6.model.pacote.hotel.cidade.estado.Estado;
import org.tech6.model.pacote.hotel.cidade.estado.regiao.Regiao;
import org.tech6.repository.pacote.PacoteRepository;
import org.tech6.repository.pacote.hotel.HotelRepository;
import org.tech6.repository.pacote.hotel.local.CidadeRepository;
import org.tech6.repository.pacote.hotel.local.EstadoRepository;
import org.tech6.repository.pacote.hotel.local.RegiaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class HotelService {

    private final HotelRepository hotelRepository;
    private final CidadeRepository cidadeRepository;
    private final EstadoRepository estadoRepository;
    private final RegiaoRepository regiaoRepository;
    private final PacoteRepository pacoteRepository;

    public HotelService(HotelRepository hotelRepository, CidadeRepository cidadeRepository,
            EstadoRepository estadoRepository, RegiaoRepository regiaoRepository,
            PacoteRepository pacoteRepository) {
        this.hotelRepository = hotelRepository;
        this.cidadeRepository = cidadeRepository;
        this.estadoRepository = estadoRepository;
        this.regiaoRepository = regiaoRepository;
        this.pacoteRepository = pacoteRepository;
    }

    // --- Métodos Auxiliares de Localização (Cascata) ---

    public List<Regiao> listarRegioes() {
        return regiaoRepository.listAll();
    }

    public List<Estado> listarEstadosPorRegiao(Long regiaoId) {
        return estadoRepository.findByRegiaoId(regiaoId);
    }

    public List<Cidade> listarCidadesPorEstado(Long estadoId) {
        return cidadeRepository.findByEstadoId(estadoId);
    }

    public List<Hotel> pegarHoteis() {
        return hotelRepository.listAll();
    }

    public Hotel pegarHotelById(long id) {
        return hotelRepository.findById(id);
    }

    // --- CRUD Hotel ---

    @Transactional
    public String criarHotel(HotelRegistroDTO hotelRegistroDTO) {
        Cidade cidadeReferencia = cidadeRepository.findById(hotelRegistroDTO.cidade());
        if (cidadeReferencia == null)
            throw new RuntimeException("Cidade não encontrada");

        Hotel hotel = new Hotel(hotelRegistroDTO, cidadeReferencia);
        hotelRepository.persist(hotel);
        return "Hotel " + hotel.nome + " criado!";
    }

    @Transactional
    public String atualizarHotel(long id, HotelRegistroDTO dto) {
        Hotel hotel = hotelRepository.findById(id);
        if (hotel == null)
            return null;

        Cidade cidade = cidadeRepository.findById(dto.cidade());
        if (cidade == null)
            throw new RuntimeException("Cidade não encontrada");

        hotel.nome = dto.nome();
        hotel.endereco = dto.endereco();
        hotel.diaria = dto.diaria();
        hotel.cidade = cidade;

        return "Hotel atualizado com sucesso!";
    }

    @Transactional
    public String deletarHotel(long id) {
        Hotel hotel = hotelRepository.findById(id);
        if (hotel == null)
            return "Não encontrado";

        // Verifica se algum pacote usa este hotel
        if (pacoteRepository.existsByHotelId(id)) {
            return "Não é possível deletar: Existem pacotes vinculados a este Hotel.";
        }

        hotelRepository.delete(hotel);
        return "Hotel deletado com sucesso.";
    }

    public List<Cidade> pegarTodasCidades() {
        return cidadeRepository.listAll();
    }

    public List<Hotel> pegarHotelPorRegiao(String regiao) {
        return hotelRepository.findByCidadeEstadoRegiaoNome(regiao);
    }

    public List<Cidade> pegarCidadePorRegiao(String regiao) {
        return cidadeRepository.findByEstadoRegiaoNome(regiao);
    }
}
