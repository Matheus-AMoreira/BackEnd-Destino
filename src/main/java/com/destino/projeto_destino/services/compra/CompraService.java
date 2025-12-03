package com.destino.projeto_destino.services.compra;

import com.destino.projeto_destino.dto.compra.CompraRequestDTO;
import com.destino.projeto_destino.dto.compra.CompraResponseDTO;
import com.destino.projeto_destino.dto.compra.ViagemDetalhadaDTO;
import com.destino.projeto_destino.dto.compra.ViagemResumoDTO;
import com.destino.projeto_destino.model.Compra;
import com.destino.projeto_destino.model.pacote.Pacote;
import com.destino.projeto_destino.model.pacote.pacoteFoto.foto.Foto;
import com.destino.projeto_destino.model.usuario.Usuario;
import com.destino.projeto_destino.repository.pacote.PacoteRepository;
import com.destino.projeto_destino.repository.usuario.CompraRepository;
import com.destino.projeto_destino.repository.usuario.UsuarioRepository;
import com.destino.projeto_destino.util.model.compra.Metodo;
import com.destino.projeto_destino.util.model.compra.Processador;
import com.destino.projeto_destino.util.model.compra.StatusCompra;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompraService {

    private final CompraRepository compraRepository;
    private final UsuarioRepository usuarioRepository;
    private final PacoteRepository pacoteRepository;

    public CompraService(CompraRepository compraRepository, UsuarioRepository usuarioRepository, PacoteRepository pacoteRepository) {
        this.compraRepository = compraRepository;
        this.usuarioRepository = usuarioRepository;
        this.pacoteRepository = pacoteRepository;
    }

    @Transactional
    public CompraResponseDTO processarCompra(CompraRequestDTO dto) {
        // 1. Buscar Usuário e Pacote
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Pacote pacote = pacoteRepository.findById(dto.pacoteId())
                .orElseThrow(() -> new RuntimeException("Pacote não encontrado"));

        // 2. Validar Disponibilidade
        if (pacote.getDisponibilidade() <= 0) {
            return new CompraResponseDTO("Pacote esgotado!", Optional.empty());
        }

        // 3. Regras de Pagamento (PIX vs Cartão)
        BigDecimal valorFinal = pacote.getPreco();
        int parcelasfinais = dto.parcelas();
        Metodo metodoFinal = dto.metodo();

        if (dto.processador() == Processador.PIX) {
            valorFinal = valorFinal.multiply(BigDecimal.valueOf(0.95)); // 5% desconto
            parcelasfinais = 1; // PIX é sempre 1x
            metodoFinal = Metodo.VISTA; // PIX é sempre à vista
        } else {
            if (parcelasfinais > 1) {
                metodoFinal = Metodo.PARCELADO;
            }
        }

        // 4. Criar a Compra
        Compra compra = new Compra();
        compra.setUsuario(usuario);
        compra.setPacote(pacote);
        compra.setDataCompra(new Date());
        compra.setMetodo(metodoFinal);
        compra.setProcessadorPagamento(dto.processador());
        compra.setParcelas(parcelasfinais); // Salva a quantidade correta
        compra.setValorFinal(valorFinal);

        // Simulação: Aprovação imediata
        compra.setStatus(StatusCompra.ACEITO);

        // 5. Atualizar Estoque do Pacote
        pacote.setDisponibilidade(pacote.getDisponibilidade() - 1);
        pacoteRepository.save(pacote);

        var compraRealizada = compraRepository.save(compra);

        return new CompraResponseDTO("Compra realizada com sucesso", Optional.of(compraRealizada));
    }

    public List<ViagemResumoDTO> listarViagensDoUsuario(String emailUsuario) {
        var usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<Compra> compras = compraRepository.findAllByUsuarioId(usuario.getId());

        return compras.stream().map(this::converterParaResumoDTO).collect(Collectors.toList());
    }

    public ViagemDetalhadaDTO buscarDetalhesViagem(Long compraId, String emailUsuario) {
        Compra compra = compraRepository.findByIdAndUsuarioEmail(compraId, emailUsuario);
        if (compra == null) {
            throw new RuntimeException("Viagem não encontrada ou acesso negado");
        }
        return converterParaDetalhadoDTO(compra);
    }

    private ViagemResumoDTO converterParaResumoDTO(Compra compra) {
        String imagem = "";
        if (compra.getPacote().getFotosDoPacote() != null && !compra.getPacote().getFotosDoPacote().getFotos().isEmpty()) {
            imagem = compra.getPacote().getFotosDoPacote().getFotos().iterator().next().getUrl();
        }

        return new ViagemResumoDTO(
                compra.getId(),
                compra.getPacote().getId(),
                compra.getPacote().getNome(),
                compra.getPacote().getDescricao(),
                compra.getValorFinal(),
                compra.getStatus().name(),
                compra.getPacote().getInicio(),
                compra.getPacote().getFim(),
                imagem,
                compra.getPacote().getHotel().getCidade().getNome(),
                compra.getPacote().getHotel().getCidade().getEstado().getSigla()
        );
    }

    private ViagemDetalhadaDTO converterParaDetalhadoDTO(Compra compra) {
        var pacote = compra.getPacote();

        List<String> galeria = new ArrayList<>();
        String imagemPrincipal = "";

        if (pacote.getFotosDoPacote() != null) {
            galeria = pacote.getFotosDoPacote().getFotos().stream()
                    .map(Foto::getUrl).collect(Collectors.toList());
            if (!galeria.isEmpty()) imagemPrincipal = galeria.get(0);
        }

        // Convertendo Date para LocalDate para consistência, caso dataCompra seja Date
        LocalDate dataCompra = compra.getDataCompra().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return new ViagemDetalhadaDTO(
                compra.getId(),
                pacote.getNome(),
                pacote.getDescricao(),
                compra.getValorFinal(),
                compra.getStatus().name(),
                pacote.getInicio(),
                pacote.getFim(),
                dataCompra,
                "RES-" + compra.getId(), // Exemplo de número de reserva
                imagemPrincipal,
                galeria,
                pacote.getTags(),
                pacote.getHotel().getNome(),
                pacote.getTransporte().getMeio().toString()
        );
    }
}
