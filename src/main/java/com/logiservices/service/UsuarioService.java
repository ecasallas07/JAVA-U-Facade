package com.logiservices.service;

import com.logiservices.model.Rol;
import com.logiservices.model.Usuario;
import com.logiservices.repository.UsuarioRepository;
import com.logiservices.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Servicio para gestión de usuarios
 *
 * Proporciona funcionalidades para crear, consultar y gestionar usuarios del sistema
 */
@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findActiveByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return UserDetailsImpl.build(usuario);
    }

    /**
     * Crea un nuevo usuario
     *
     * @param usuario Usuario a crear
     * @return Usuario creado
     */
    public Usuario crearUsuario(Usuario usuario) {
        // Encriptar la contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Establecer fecha de creación
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setActivo(true);

        return usuarioRepository.save(usuario);
    }

    /**
     * Busca un usuario por ID
     *
     * @param id ID del usuario
     * @return Optional con el usuario encontrado
     */
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Busca un usuario por nombre de usuario
     *
     * @param username Nombre de usuario
     * @return Optional con el usuario encontrado
     */
    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    /**
     * Busca un usuario por email
     *
     * @param email Email del usuario
     * @return Optional con el usuario encontrado
     */
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Obtiene todos los usuarios
     *
     * @return Lista de usuarios
     */
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    /**
     * Actualiza la información de un usuario
     *
     * @param usuario Usuario a actualizar
     * @return Usuario actualizado
     */
    public Usuario actualizarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    /**
     * Actualiza el último acceso de un usuario
     *
     * @param username Nombre de usuario
     */
    public void actualizarUltimoAcceso(String username) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setUltimoAcceso(LocalDateTime.now());
            usuarioRepository.save(usuario);
        }
    }

    /**
     * Desactiva un usuario
     *
     * @param id ID del usuario
     * @return true si se desactivó, false si no se encontró
     */
    public boolean desactivarUsuario(Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setActivo(false);
            usuarioRepository.save(usuario);
            return true;
        }
        return false;
    }

    /**
     * Activa un usuario
     *
     * @param id ID del usuario
     * @return true si se activó, false si no se encontró
     */
    public boolean activarUsuario(Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setActivo(true);
            usuarioRepository.save(usuario);
            return true;
        }
        return false;
    }

    /**
     * Verifica si existe un usuario con el nombre de usuario dado
     *
     * @param username Nombre de usuario
     * @return true si existe, false en caso contrario
     */
    public boolean existsByUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    /**
     * Verifica si existe un usuario con el email dado
     *
     * @param email Email del usuario
     * @return true si existe, false en caso contrario
     */
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    /**
     * Crea usuarios de ejemplo para testing
     */
    public void crearUsuariosEjemplo() {
        // Solo crear si no existen usuarios
        if (usuarioRepository.count() == 0) {

            // Usuario Administrador
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setEmail("admin@logiservices.com");
            admin.setPassword("admin123");
            admin.setNombreCompleto("Administrador del Sistema");
            Set<Rol> rolesAdmin = new HashSet<>();
            rolesAdmin.add(Rol.ADMIN);
            admin.setRoles(rolesAdmin);
            crearUsuario(admin);

            // Usuario Operador
            Usuario operador = new Usuario();
            operador.setUsername("operador");
            operador.setEmail("operador@logiservices.com");
            operador.setPassword("operador123");
            operador.setNombreCompleto("Operador de Logística");
            Set<Rol> rolesOperador = new HashSet<>();
            rolesOperador.add(Rol.OPERADOR);
            operador.setRoles(rolesOperador);
            crearUsuario(operador);

            // Usuario Consultor
            Usuario consultor = new Usuario();
            consultor.setUsername("consultor");
            consultor.setEmail("consultor@logiservices.com");
            consultor.setPassword("consultor123");
            consultor.setNombreCompleto("Consultor de Envíos");
            Set<Rol> rolesConsultor = new HashSet<>();
            rolesConsultor.add(Rol.CONSULTOR);
            consultor.setRoles(rolesConsultor);
            crearUsuario(consultor);

            // Usuario Cliente
            Usuario cliente = new Usuario();
            cliente.setUsername("cliente");
            cliente.setEmail("cliente@logiservices.com");
            cliente.setPassword("cliente123");
            cliente.setNombreCompleto("Cliente Empresarial");
            Set<Rol> rolesCliente = new HashSet<>();
            rolesCliente.add(Rol.CLIENTE);
            cliente.setRoles(rolesCliente);
            crearUsuario(cliente);
        }
    }
}
