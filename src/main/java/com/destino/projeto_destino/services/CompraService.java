package com.destino.projeto_destino.services;

import com.destino.projeto_destino.dto.compra.CompraRequestDTO;
import com.destino.projeto_destino.model.Compra;
import com.destino.projeto_destino.model.pacote.Pacote;
import com.destino.projeto_destino.model.usuario.Usuario;
import com.destino.projeto_destino.repository.CompraRepository;
import com.destino.projeto_destino.repository.UsuarioRepository;
import com.destino.projeto_destino.repository.pacote.PacoteRepository;
import com.destino.projeto_destino.util.compra.Processador;
import com.destino.projeto_destino.util.compra.StatusCompra;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

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
    public ResponseEntity<String> processarCompra(CompraRequestDTO dto) {
        // 1. Buscar Usuário e Pacote
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Pacote pacote = pacoteRepository.findById(dto.pacoteId())
                .orElseThrow(() -> new RuntimeException("Pacote não encontrado"));

        // 2. Validar Disponibilidade
        if (pacote.getDisponibilidade() <= 0) {
            return ResponseEntity.badRequest().body("Pacote esgotado!");
        }

        // 3. Calcular Valor Final (Regra de Negócio: 5% desconto no PIX)
        BigDecimal valorFinal = pacote.getPreco();
        if (dto.processador() == Processador.PIX) {
            valorFinal = valorFinal.multiply(BigDecimal.valueOf(0.95)); // 5% desconto
        }

        // 4. Criar a Compra
        Compra compra = new Compra();
        compra.setUsuario(usuario);
        compra.setPacote(pacote);
        compra.setDataCompra(new Date());
        compra.setMetodo(dto.metodo());
        compra.setProcessadorPagamento(dto.processador());
        compra.setValorFinal(valorFinal);

        // Simulação: Aprovação imediata para fins didáticos
        compra.setStatus(StatusCompra.ACEITO);

        // 5. Atualizar Estoque do Pacote
        pacote.setDisponibilidade(pacote.getDisponibilidade() - 1);
        pacoteRepository.save(pacote);

        compraRepository.save(compra);

        return ResponseEntity.ok(compra.getId().toString());
    }
}
