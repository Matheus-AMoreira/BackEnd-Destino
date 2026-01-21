package com.fatec.destino.repository.usuario;

import com.fatec.destino.model.usuario.Usuario;
import com.fatec.destino.util.model.usuario.cpf.Cpf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByCpf(Cpf cpf);

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByValidoFalse();

    @Modifying
    @Query("UPDATE Usuario u SET u.valido = true WHERE u.id = :id")
    int validarUsuario(@Param("id") UUID id);
}
