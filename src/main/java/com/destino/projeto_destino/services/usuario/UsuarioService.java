package com.destino.projeto_destino.services.usuario;

import com.destino.projeto_destino.dto.UsuarioDTO;
import com.destino.projeto_destino.dto.auth.validar.ValidarResponseDTO;
import com.destino.projeto_destino.model.usuario.Usuario;
import com.destino.projeto_destino.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

        List<UsuarioDTO> usuariosDTO = usuarios.stream().map(usuario -> new UsuarioDTO(
                usuario.getId(),
                usuario.getNome() + " " + usuario.getSobreNome(),
                usuario.getCpf().getValorFormatado(),
                usuario.getEmail(),
                usuario.getTelefone().getValorFormatado(),
                usuario.getPerfil(),
                usuario.getValido(),
                usuario.getCadastro(),
                usuario.getCadastro())).collect(Collectors.toList());

        return ResponseEntity.ok().body(usuariosDTO);
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
