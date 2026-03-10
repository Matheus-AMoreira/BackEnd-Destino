package org.tech6.services.compra;

import org.tech6.dto.compra.CompraRequestDTO;
import org.tech6.dto.compra.CompraResponseDTO;
import org.tech6.dto.compra.ViagemDetalhadaDTO;
import org.tech6.dto.compra.ViagemResumoDTO;
import org.tech6.model.Compra;
import org.tech6.model.pacote.Pacote;
import org.tech6.model.pacote.pacoteFoto.foto.Foto;
import org.tech6.model.pacote.tag.Tag;
import org.tech6.model.usuario.Usuario;
import org.tech6.model.pacote.oferta.Oferta;
import org.tech6.repository.pacote.oferta.OfertaRepository;
import org.tech6.repository.usuario.CompraRepository;
import org.tech6.repository.usuario.UsuarioRepository;
import org.tech6.util.model.compra.Metodo;
import org.tech6.util.model.compra.Processador;
import org.tech6.util.model.compra.StatusCompra;
import org.tech6.util.model.pacote.OfertaStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class CompraService {

    private final CompraRepository compraRepository;
    private final UsuarioRepository usuarioRepository;
    private final OfertaRepository ofertaRepository;

    public CompraService(CompraRepository compraRepository, UsuarioRepository usuarioRepository,
            OfertaRepository ofertaRepository) {
        this.compraRepository = compraRepository;
        this.usuarioRepository = usuarioRepository;
        this.ofertaRepository = ofertaRepository;
    }

    @Transactional
    public CompraResponseDTO processarCompra(CompraRequestDTO dto) {
        // 1. Buscar Usuário e Oferta
        Usuario usuario = usuarioRepository.findById(dto.usuarioId());
        if (usuario == null)
            throw new RuntimeException("Usuário não encontrado");

        Oferta oferta = ofertaRepository.findById(dto.ofertaId());
        if (oferta == null)
            throw new RuntimeException("Oferta não encontrada");

        // 2. Validar Disponibilidade
        if (oferta.disponibilidade <= 0) {
            return new CompraResponseDTO("Oferta esgotada!", null);
        }

        // 3. Regras de Pagamento (PIX vs Cartão)
        BigDecimal valorFinal = oferta.preco;
        int parcelasfinais = dto.parcelas();
        Metodo metodoFinal = dto.metodo();

        if (dto.processador() == Processador.PIX) {
            valorFinal = valorFinal.multiply(BigDecimal.valueOf(0.95)); // 5% desconto
            parcelasfinais = 1; // PIX é sempre 1x
            metodoFinal = Metodo.VISTA; // PIX é always upfront/full amount
        } else {
            if (parcelasfinais > 1) {
                metodoFinal = Metodo.PARCELADO;
            }
        }

        // 4. Criar a Compra
        Compra compra = new Compra();
        compra.usuario = usuario;
        compra.oferta = oferta;
        compra.dataCompra = new Date();
        compra.metodo = metodoFinal;
        compra.processadorPagamento = dto.processador();
        compra.parcelas = parcelasfinais; // Salva a quantidade correta
        compra.valorFinal = valorFinal;

        // Simulação: Aprovação imediata
        compra.status = StatusCompra.ACEITO;

        // 5. Atualizar Estoque da Oferta
        oferta.disponibilidade = oferta.disponibilidade - 1;
        // Panache automanages updates

        compraRepository.persist(compra);

        return new CompraResponseDTO("Compra realizada com sucesso", compra);
    }

    public List<ViagemResumoDTO> listarViagensEmAndamentoDoUsuario(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario).orElse(null);
        if (usuario == null)
            throw new RuntimeException("Usuário não encontrado");

        List<Compra> compras = compraRepository.findAllByUsuarioIdWhereStatusEmAdamento(usuario.id,
                OfertaStatus.EMANDAMENTO);

        return compras.stream().map(this::converterParaResumoDTO).collect(Collectors.toList());
    }

    public List<ViagemResumoDTO> listarViagensConcluidasDoUsuarios(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario).orElse(null);
        if (usuario == null)
            throw new RuntimeException("Usuário não encontrado");

        List<Compra> compras = compraRepository.findAllByUsuarioIdWhereStatusConcluido(usuario.id,
                OfertaStatus.CONCLUIDO);

        return compras.stream().map(this::converterParaResumoDTO).collect(Collectors.toList());
    }

    public ViagemDetalhadaDTO buscarDetalhesViagem(UUID compraId, String emailUsuario) {
        Compra compra = compraRepository.findByIdAndUsuarioEmail(compraId, emailUsuario);
        if (compra == null) {
            throw new RuntimeException("Viagem não encontrada ou acesso negado");
        }
        return converterParaDetalhadoDTO(compra);
    }

    private ViagemResumoDTO converterParaResumoDTO(Compra compra) {
        String imagem = "";
        Pacote pacote = compra.oferta.pacote;
        if (pacote.fotosDoPacote != null && pacote.fotosDoPacote.fotoDoPacote != null && !pacote.fotosDoPacote.fotoDoPacote.isEmpty()) {
            imagem = pacote.fotosDoPacote.fotoDoPacote;
        } else if (pacote.fotosDoPacote != null && pacote.fotosDoPacote.fotos != null && !pacote.fotosDoPacote.fotos.isEmpty()) {
            imagem = pacote.fotosDoPacote.fotos.iterator().next().url;
        }

        return new ViagemResumoDTO(
                compra.id,
                pacote.id,
                pacote.nome,
                pacote.descricao,
                compra.valorFinal,
                compra.status.name(),
                compra.oferta.inicio,
                compra.oferta.fim,
                imagem,
                compra.oferta.hotel.cidade.nome,
                compra.oferta.hotel.cidade.estado.sigla);
    }

    private ViagemDetalhadaDTO converterParaDetalhadoDTO(Compra compra) {
        var oferta = compra.oferta;
        var pacote = oferta.pacote;

        List<String> galeria = new ArrayList<>();
        String imagemPrincipal = "";

        if (pacote.fotosDoPacote != null) {
            if (pacote.fotosDoPacote.fotoDoPacote != null && !pacote.fotosDoPacote.fotoDoPacote.isEmpty()) {
                imagemPrincipal = pacote.fotosDoPacote.fotoDoPacote;
            }
            if (pacote.fotosDoPacote.fotos != null) {
                galeria = pacote.fotosDoPacote.fotos.stream()
                        .map(foto -> foto.url).collect(Collectors.toList());
                if (imagemPrincipal.isEmpty() && !galeria.isEmpty()) {
                    imagemPrincipal = galeria.getFirst();
                }
            }
        }

        // Convertendo Date para LocalDate para consistência, caso dataCompra seja Date
        LocalDate dataCompra = compra.dataCompra.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return new ViagemDetalhadaDTO(
                compra.id,
                pacote.nome,
                pacote.descricao,
                compra.valorFinal,
                compra.status.name(),
                oferta.inicio,
                oferta.fim,
                dataCompra,
                "RES-" + compra.id,
                imagemPrincipal,
                galeria,
                pacote.tags.stream().map(Tag::getNome)
                        .collect(Collectors.toList()),
                oferta.hotel.nome,
                oferta.transporte.meio.toString());
    }
}
