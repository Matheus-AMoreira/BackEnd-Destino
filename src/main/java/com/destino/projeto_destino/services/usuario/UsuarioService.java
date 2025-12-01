package com.destino.projeto_destino.services.usuario;

import com.destino.projeto_destino.dto.usuario.UsuarioDTO;
import com.destino.projeto_destino.dto.auth.validar.ValidarResponseDTO;
import com.destino.projeto_destino.model.usuario.Usuario;
import com.destino.projeto_destino.repository.usuario.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository userRepository;

    public UsuarioService(UsuarioRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Iterable<Usuario> buscarUsuarios() {
        return userRepository.findAll();
    }

    public ResponseEntity<List<UsuarioDTO>> inValidUsers() {
        List<Usuario> usuarios = userRepository.findByValidoFalse();
        List<UsuarioDTO> users = usuarios.stream().map(usuario -> new UsuarioDTO(
                usuario.getId().toString(),
                usuario.getNome() + " " + usuario.getSobreNome(),
                usuario.getCpf().getValorFormatado(),
                usuario.getEmail(),
                usuario.getTelefone().getValorFormatado(),
                usuario.getPerfil().name(),
                usuario.getValido() == true ? "Sim" : "Não",
                usuario.getCadastro()
        )).toList();
        return ResponseEntity.ok().body(users);
    }

    @Transactional
    public ResponseEntity<ValidarResponseDTO> validar(UUID id) {
        int linhasAfetadas = userRepository.validarUsuario(id);

        if (linhasAfetadas == 0) {
            return ResponseEntity.ok().body(new ValidarResponseDTO(true, "Não existe usuario com id: " + id));
        }

        return ResponseEntity.ok().body(new ValidarResponseDTO(false, "Usuário atualizado com sucesso!"));
    }
}
