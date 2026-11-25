package com.example.saaapi.model.repository;

import com.example.saaapi.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("select usuario from Usuario usuario where usuario.login = :login")
    Optional<Usuario> buscarPorLogin(@Param("login") String login);

    @Query("select count(usuario) from Usuario usuario where usuario.login = :login " +
            "and (:id is null or usuario.id <> :id)")
    long contarOutrosUsuariosPorLogin(@Param("login") String login, @Param("id") Long id);
}
