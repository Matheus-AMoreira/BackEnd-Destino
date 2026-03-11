package org.tech6.services.pacote;

import org.tech6.dto.pacote.OfertaRegistroDTO;
import org.tech6.model.pacote.Pacote;
import org.tech6.model.pacote.oferta.Oferta;
import org.tech6.model.pacote.hotel.Hotel;
import org.tech6.model.pacote.transporte.Transporte;
import org.tech6.repository.pacote.hotel.HotelRepository;
import org.tech6.repository.pacote.TransporteRepository;
import org.tech6.repository.pacote.oferta.OfertaRepository;
import org.tech6.repository.pacote.PacoteRepository;
import org.tech6.util.model.pacote.OfertaStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
@ApplicationScoped
public class OfertaService {

    private final OfertaRepository ofertaRepository;
    private final PacoteRepository pacoteRepository;
    private final HotelRepository hotelRepository;
    private final TransporteRepository transporteRepository;

    public OfertaService(OfertaRepository ofertaRepository, PacoteRepository pacoteRepository,
                         HotelRepository hotelRepository, TransporteRepository transporteRepository) {
        this.ofertaRepository = ofertaRepository;
        this.pacoteRepository = pacoteRepository;
        this.hotelRepository = hotelRepository;
        this.transporteRepository = transporteRepository;
    }

    @Transactional
    public String registrarOferta(OfertaRegistroDTO dto) {
        Pacote pacote = pacoteRepository.findById(dto.pacoteId());
        if (pacote == null) {
            throw new RuntimeException("Pacote não encontrado");
        }

        Hotel hotel = hotelRepository.findById(dto.hotelId());
        if (hotel == null) {
            throw new RuntimeException("Hotel não encontrado");
        }

        Transporte transporte = transporteRepository.findById(dto.transporteId());
        if (transporte == null) {
            throw new RuntimeException("Transporte não encontrado");
        }
        
        // Validation: Unique/non-overlapping dates for the same package
        if (pacote.ofertas != null && !pacote.ofertas.isEmpty()) {
            for (Oferta o : pacote.ofertas) {
                if ((dto.inicio().isBefore(o.fim) || dto.inicio().isEqual(o.fim)) &&
                    (dto.fim().isAfter(o.inicio) || dto.fim().isEqual(o.inicio))) {
                    throw new RuntimeException("As datas da oferta conflitam com uma data já cadastrada neste pacote.");
                }
            }
        }

        Oferta oferta = new Oferta();
        oferta.preco = dto.preco();
        oferta.inicio = dto.inicio();
        oferta.fim = dto.fim();
        oferta.disponibilidade = dto.disponibilidade();
        oferta.status = OfertaStatus.EMANDAMENTO;
        oferta.pacote = pacote;
        oferta.hotel = hotel;
        oferta.transporte = transporte;

        ofertaRepository.persist(oferta);

        return "Oferta registrada com sucesso!";
    }

    @Transactional
    public String atualizarOferta(long id, OfertaRegistroDTO dto) {
        Oferta oferta = ofertaRepository.findById(id);
        if (oferta == null) {
            throw new RuntimeException("Oferta não encontrada");
        }

        Pacote pacote = pacoteRepository.findById(dto.pacoteId());
        if (pacote == null) {
            throw new RuntimeException("Pacote não encontrado");
        }

        Hotel hotel = hotelRepository.findById(dto.hotelId());
        if (hotel == null) {
            throw new RuntimeException("Hotel não encontrado");
        }

        Transporte transporte = transporteRepository.findById(dto.transporteId());
        if (transporte == null) {
            throw new RuntimeException("Transporte não encontrado");
        }

        // Validation for date overlapping ignoring this specific Oferta
        if (pacote.ofertas != null && !pacote.ofertas.isEmpty()) {
            for (Oferta o : pacote.ofertas) {
                if (o.id == oferta.id) {
                    continue; // Skip itself
                }
                if ((dto.inicio().isBefore(o.fim) || dto.inicio().isEqual(o.fim)) &&
                    (dto.fim().isAfter(o.inicio) || dto.fim().isEqual(o.inicio))) {
                    throw new RuntimeException("As datas da oferta editada conflitam com uma data já cadastrada neste pacote.");
                }
            }
        }

        oferta.preco = dto.preco();
        oferta.inicio = dto.inicio();
        oferta.fim = dto.fim();
        oferta.disponibilidade = dto.disponibilidade();
        oferta.pacote = pacote;
        oferta.hotel = hotel;
        oferta.transporte = transporte;

        return "Oferta atualizada com sucesso!";
    }

    @Transactional
    public String deletarOferta(long id) {
        boolean deleted = ofertaRepository.deleteById(id);
        if (!deleted) {
            throw new RuntimeException("Oferta não encontrada");
        }
        return "Oferta deletada com sucesso!";
    }
}
