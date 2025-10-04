package com.logiservices.controller;

import com.logiservices.dto.JwtResponse;
import com.logiservices.dto.LoginRequest;
import com.logiservices.model.Usuario;
import com.logiservices.security.JwtUtil;
import com.logiservices.security.UserDetailsImpl;
import com.logiservices.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controller para autenticación y autorización
 *
 * Maneja el login de usuarios y la generación de tokens JWT
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Endpoints para autenticación y autorización")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Endpoint para autenticación de usuarios
     *
     * @param loginRequest Datos de login (username y password)
     * @return Token JWT y información del usuario
     */
    @PostMapping("/login")
    @Operation(summary = "Autenticar usuario",
               description = "Autentica un usuario y devuelve un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Autenticación exitosa",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas",
                    content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Autenticar al usuario
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            // Establecer la autenticación en el contexto
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Obtener los detalles del usuario
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // Generar el token JWT
            String jwt = jwtUtil.generateToken(userDetails);

            // Actualizar el último acceso
            usuarioService.actualizarUltimoAcceso(userDetails.getUsername());

            // Calcular la fecha de expiración
            LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(jwtUtil.getExpirationTime() / 1000);

            // Crear la respuesta
            JwtResponse jwtResponse = new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getNombreCompleto(),
                userDetails.getAuthorities().stream()
                    .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                    .map(com.logiservices.model.Rol::valueOf)
                    .collect(java.util.stream.Collectors.toSet()),
                expiresAt
            );

            return ResponseEntity.ok(jwtResponse);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Credenciales inválidas", "message", e.getMessage()));
        }
    }

    /**
     * Endpoint para obtener información del usuario autenticado
     *
     * @return Información del usuario actual
     */
    @GetMapping("/me")
    @Operation(summary = "Información del usuario actual",
               description = "Obtiene la información del usuario autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Información del usuario",
                    content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<?> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No autenticado"));
            }

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            return ResponseEntity.ok(Map.of(
                "id", userDetails.getId(),
                "username", userDetails.getUsername(),
                "email", userDetails.getEmail(),
                "nombreCompleto", userDetails.getNombreCompleto(),
                "roles", userDetails.getAuthorities().stream()
                    .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                    .collect(java.util.stream.Collectors.toList()),
                "enabled", userDetails.isEnabled()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "No se pudo obtener la información del usuario", "message", e.getMessage()));
        }
    }

    /**
     * Endpoint para validar un token JWT
     *
     * @param token Token JWT a validar
     * @return Resultado de la validación
     */
    @PostMapping("/validate")
    @Operation(summary = "Validar token JWT",
               description = "Valida si un token JWT es válido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token válido",
                    content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "401", description = "Token inválido",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");

            if (token == null || token.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Token requerido"));
            }

            boolean isValid = jwtUtil.validateToken(token);

            if (isValid) {
                String username = jwtUtil.extractUsername(token);
                return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "username", username,
                    "message", "Token válido"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "message", "Token inválido o expirado"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("valid", false, "error", "Error al validar token", "message", e.getMessage()));
        }
    }

    /**
     * Endpoint para obtener información sobre la autenticación
     *
     * @return Información del sistema de autenticación
     */
    @GetMapping("/info")
    @Operation(summary = "Información de autenticación",
               description = "Obtiene información sobre el sistema de autenticación")
    public ResponseEntity<?> getAuthInfo() {
        return ResponseEntity.ok(Map.of(
            "sistema", "LogiServices Authentication",
            "version", "1.0.0",
            "tipo", "JWT (JSON Web Token)",
            "endpoints", Map.of(
                "POST /api/v1/auth/login", "Autenticar usuario",
                "GET /api/v1/auth/me", "Información del usuario actual",
                "POST /api/v1/auth/validate", "Validar token JWT",
                "GET /api/v1/auth/info", "Información del sistema de autenticación"
            ),
            "usuariosEjemplo", Map.of(
                "admin", "admin123 (Administrador)",
                "operador", "operador123 (Operador)",
                "consultor", "consultor123 (Consultor)",
                "cliente", "cliente123 (Cliente)"
            ),
            "instrucciones", Map.of(
                "1", "Hacer POST a /api/v1/auth/login con username y password",
                "2", "Copiar el token de la respuesta",
                "3", "Incluir en header: Authorization: Bearer <token>",
                "4", "Usar en todas las peticiones protegidas"
            )
        ));
    }
}
