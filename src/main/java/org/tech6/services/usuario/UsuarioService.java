package org.tech6.services.usuario;

import org.tech6.dto.usuario.UsuarioDTO;
import org.tech6.dto.auth.validar.ValidarResponseDTO;
import org.tech6.model.usuario.Usuario;
import org.tech6.repository.usuario.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UsuarioService {

    private final UsuarioRepository userRepository;

    public UsuarioService(UsuarioRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<Usuario> buscarUsuarios() {
        return userRepository.listAll();
    }

    public List<UsuarioDTO> inValidUsers() {
        List<Usuario> usuarios = userRepository.findByValidoFalse();
        return usuarios.stream().map(usuario -> new UsuarioDTO(
                usuario.id.toString(),
                usuario.nome + " " + usuario.sobreNome,
                usuario.cpf.getValorFormatado(),
                usuario.email,
                usuario.telefone.getValorFormatado(),
                usuario.role.name(),
                usuario.valido == true ? "Sim" : "Não",
                usuario.cadastro)).toList();
    }

    @Transactional
    public ValidarResponseDTO validar(UUID id) {
        int linhasAfetadas = userRepository.validarUsuario(id);

        if (linhasAfetadas == 0) {
            return new ValidarResponseDTO(true, "Não existe usuario com id: " + id);
        }

        return new ValidarResponseDTO(false, "Usuário atualizado com sucesso!");
    }
}
