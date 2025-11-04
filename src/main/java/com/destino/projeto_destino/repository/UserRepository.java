package com.destino.projeto_destino.repository;

import com.destino.projeto_destino.model.Usuario;
import com.destino.projeto_destino.model.usuarioUtils.Cpf.Cpf;
import com.destino.projeto_destino.model.usuarioUtils.Email.Email;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<Usuario, UUID> {
    Optional<Usuario> findByCpf(Cpf cpf);
    Optional<Usuario> findByEmail(Email email);
}
