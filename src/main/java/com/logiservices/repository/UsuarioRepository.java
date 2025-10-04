package com.logiservices.repository;

import com.logiservices.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Usuario
 *
 * Proporciona métodos para acceder a los datos de usuarios en la base de datos
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por nombre de usuario
     *
     * @param username Nombre de usuario
     * @return Optional con el usuario encontrado
     */
    Optional<Usuario> findByUsername(String username);

    /**
     * Busca un usuario por email
     *
     * @param email Email del usuario
     * @return Optional con el usuario encontrado
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el nombre de usuario dado
     *
     * @param username Nombre de usuario
     * @return true si existe, false en caso contrario
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si existe un usuario con el email dado
     *
     * @param email Email del usuario
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuarios activos por nombre de usuario
     *
     * @param username Nombre de usuario
     * @return Optional con el usuario activo encontrado
     */
    @Query("SELECT u FROM Usuario u WHERE u.username = :username AND u.activo = true")
    Optional<Usuario> findActiveByUsername(@Param("username") String username);

    /**
     * Busca usuarios activos por email
     *
     * @param email Email del usuario
     * @return Optional con el usuario activo encontrado
     */
    @Query("SELECT u FROM Usuario u WHERE u.email = :email AND u.activo = true")
    Optional<Usuario> findActiveByEmail(@Param("email") String email);
}
