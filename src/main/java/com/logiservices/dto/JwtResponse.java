package com.logiservices.dto;

import com.logiservices.model.Rol;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO para las respuestas de autenticación JWT
 */
@Schema(description = "Respuesta de autenticación exitosa")
public class JwtResponse {

    @Schema(description = "Token JWT para autenticación", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Tipo de token", example = "Bearer")
    private String type = "Bearer";

    @Schema(description = "ID del usuario", example = "1")
    private Long id;

    @Schema(description = "Nombre de usuario", example = "admin")
    private String username;

    @Schema(description = "Email del usuario", example = "admin@logiservices.com")
    private String email;

    @Schema(description = "Nombre completo del usuario", example = "Administrador del Sistema")
    private String nombreCompleto;

    @Schema(description = "Roles del usuario")
    private Set<Rol> roles;

    @Schema(description = "Fecha de expiración del token")
    private LocalDateTime expiresAt;

    // Constructores
    public JwtResponse() {}

    public JwtResponse(String token, Long id, String username, String email,
                      String nombreCompleto, Set<Rol> roles, LocalDateTime expiresAt) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.nombreCompleto = nombreCompleto;
        this.roles = roles;
        this.expiresAt = expiresAt;
    }

    // Getters y Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public Set<Rol> getRoles() {
        return roles;
    }

    public void setRoles(Set<Rol> roles) {
        this.roles = roles;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public String toString() {
        return "JwtResponse{" +
                "type='" + type + '\'' +
                ", id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", roles=" + roles +
                ", expiresAt=" + expiresAt +
                '}';
    }
}
