package com.logiservices.controller;

import com.logiservices.dto.EnvioDto;
import com.logiservices.service.EnvioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para el endpoint principal del Service Facade
 *
 * Este controller expone el endpoint unificado /api/v1/envios/{id}
 * que funciona como punto de entrada del Service Facade para LogiServices.
 *
 * @RestController: Marca la clase como controller REST
 * @RequestMapping: Define la ruta base para todos los endpoints
 */
@RestController
@RequestMapping("/api/v1/envios")
@Tag(name = "Envíos", description = "Gestión de envíos y consultas del Service Facade")
@SecurityRequirement(name = "bearerAuth")
public class EnvioController {

    private final EnvioService envioService;

    /**
     * Constructor con inyección de dependencias
     * Spring Boot inyecta automáticamente el EnvioService
     */
    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }

    /**
     * Endpoint principal: GET /api/v1/envios/{id}
     *
     * Este es el punto de entrada unificado del Service Facade que:
     * 1. Recibe un ID de envío
     * 2. Consulta el servicio correspondiente (TMS, ACMS, SMCS)
     * 3. Devuelve la información unificada del envío
     *
     * @param id ID del envío (como String desde la URL)
     * @return ResponseEntity con la información del envío o error
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener envío por ID",
               description = "Consulta un envío por su ID en todos los sistemas (TMS, ACMS, SMCS)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Envío encontrado",
                    content = @Content(schema = @Schema(implementation = EnvioDto.class))),
        @ApiResponse(responseCode = "404", description = "Envío no encontrado",
                    content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "400", description = "ID inválido",
                    content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "403", description = "Sin permisos",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'CONSULTOR', 'CLIENTE')")
    public ResponseEntity<?> getEnvioById(
            @Parameter(description = "ID del envío", example = "123", required = true)
            @PathVariable("id") String id) {
        try {
            // Convertir el ID de String a Long
            Long envioId = Long.parseLong(id);

            // PRIMERO: Intentar buscar en sistemas integrados (TMS, ACMS, SMCS)
            Map<String, Object> envioSistema = envioService.consultarEnvioEnSistema(envioId);
            if (envioSistema != null && !envioSistema.containsKey("error")) {
                return ResponseEntity.ok(envioSistema);
            }

            // SEGUNDO: Si no se encuentra en sistemas, buscar en datos locales
            EnvioDto envio = envioService.getEnvioById(envioId);
            if (envio != null) {
                return ResponseEntity.ok(envio);
            }

            // Si no se encuentra en ningún lado, devolver 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró información del envío con ID " + envioId);

        } catch (NumberFormatException e) {
            // Si el ID no es numérico, devolver 400 Bad Request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("El ID del envío debe ser numérico.");
        }
    }

    /**
     * CREAR - POST /api/v1/envios
     * Crea un nuevo envío
     *
     * @param envioDto Datos del envío a crear (en el body del request)
     * @return ResponseEntity con el envío creado o error
     */
    @PostMapping
    public ResponseEntity<?> crearEnvio(@RequestBody EnvioDto envioDto) {
        try {
            // Validaciones básicas
            if (envioDto.getOrigen() == null || envioDto.getOrigen().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El origen es obligatorio"));
            }

            if (envioDto.getDestino() == null || envioDto.getDestino().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El destino es obligatorio"));
            }

            if (envioDto.getEstado() == null || envioDto.getEstado().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El estado es obligatorio"));
            }

            if (envioDto.getSistemaOrigen() == null || envioDto.getSistemaOrigen().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El sistema de origen es obligatorio"));
            }

            // Validar que el sistema sea válido
            String sistema = envioDto.getSistemaOrigen().toUpperCase();
            if (!sistema.equals("TMS") && !sistema.equals("ACMS") && !sistema.equals("SMCS")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El sistema de origen debe ser TMS, ACMS o SMCS"));
            }

            // Normalizar el sistema a mayúsculas
            envioDto.setSistemaOrigen(sistema);

            // Crear el envío
            EnvioDto nuevoEnvio = envioService.crearEnvio(envioDto);

            // Devolver 201 Created con el envío creado
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEnvio);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor: " + e.getMessage()));
        }
    }

    /**
     * ACTUALIZAR - PUT /api/v1/envios/{id}
     * Actualiza un envío existente
     *
     * @param id ID del envío a actualizar
     * @param envioDto Datos nuevos del envío
     * @return ResponseEntity con el envío actualizado o error
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEnvio(@PathVariable("id") String id,
                                           @RequestBody EnvioDto envioDto) {
        try {
            // Convertir el ID de String a Long
            Long envioId = Long.parseLong(id);

            // Validaciones básicas
            if (envioDto.getOrigen() == null || envioDto.getOrigen().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El origen es obligatorio"));
            }

            if (envioDto.getDestino() == null || envioDto.getDestino().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El destino es obligatorio"));
            }

            if (envioDto.getEstado() == null || envioDto.getEstado().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El estado es obligatorio"));
            }

            if (envioDto.getSistemaOrigen() == null || envioDto.getSistemaOrigen().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El sistema de origen es obligatorio"));
            }

            // Validar que el sistema sea válido
            String sistema = envioDto.getSistemaOrigen().toUpperCase();
            if (!sistema.equals("TMS") && !sistema.equals("ACMS") && !sistema.equals("SMCS")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El sistema de origen debe ser TMS, ACMS o SMCS"));
            }

            // Normalizar el sistema a mayúsculas
            envioDto.setSistemaOrigen(sistema);

            // Actualizar el envío
            EnvioDto envioActualizado = envioService.actualizarEnvio(envioId, envioDto);

            // Devolver 200 OK con el envío actualizado
            return ResponseEntity.ok(envioActualizado);

        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "El ID del envío debe ser numérico"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor: " + e.getMessage()));
        }
    }

    /**
     * ELIMINAR - DELETE /api/v1/envios/{id}
     * Elimina un envío existente
     *
     * @param id ID del envío a eliminar
     * @return ResponseEntity con confirmación o error
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEnvio(@PathVariable("id") String id) {
        try {
            // Convertir el ID de String a Long
            Long envioId = Long.parseLong(id);

            // Eliminar el envío
            EnvioDto envioEliminado = envioService.eliminarEnvio(envioId);

            // Devolver 200 OK con el envío eliminado
            return ResponseEntity.ok(Map.of(
                "mensaje", "Envío eliminado correctamente",
                "envioEliminado", envioEliminado
            ));

        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "El ID del envío debe ser numérico"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor: " + e.getMessage()));
        }
    }

    /**
     * LISTAR TODOS - GET /api/v1/envios
     * Obtiene todos los envíos
     *
     * @return ResponseEntity con lista de envíos
     */
    @GetMapping
    public ResponseEntity<?> obtenerTodosLosEnvios() {
        try {
            List<EnvioDto> envios = envioService.getAllEnviosList();
            return ResponseEntity.ok(Map.of(
                "total", envios.size(),
                "envios", envios
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor: " + e.getMessage()));
        }
    }

    /**
     * BUSCAR POR SISTEMA - GET /api/v1/envios/sistema/{sistema}
     * Busca envíos por sistema de origen
     *
     * @param sistema Sistema a buscar (TMS, ACMS, SMCS)
     * @return ResponseEntity con lista de envíos del sistema
     */
    @GetMapping("/sistema/{sistema}")
    public ResponseEntity<?> buscarPorSistema(@PathVariable("sistema") String sistema) {
        try {
            String sistemaUpper = sistema.toUpperCase();

            // Validar que el sistema sea válido
            if (!sistemaUpper.equals("TMS") && !sistemaUpper.equals("ACMS") && !sistemaUpper.equals("SMCS")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El sistema debe ser TMS, ACMS o SMCS"));
            }

            List<EnvioDto> envios = envioService.buscarPorSistemaOrigen(sistemaUpper);

            return ResponseEntity.ok(Map.of(
                "sistema", sistemaUpper,
                "total", envios.size(),
                "envios", envios
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor: " + e.getMessage()));
        }
    }

    /**
     * BUSCAR POR ESTADO - GET /api/v1/envios/estado/{estado}
     * Busca envíos por estado
     *
     * @param estado Estado a buscar
     * @return ResponseEntity con lista de envíos con ese estado
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> buscarPorEstado(@PathVariable("estado") String estado) {
        try {
            List<EnvioDto> envios = envioService.buscarPorEstado(estado);

            return ResponseEntity.ok(Map.of(
                "estado", estado,
                "total", envios.size(),
                "envios", envios
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor: " + e.getMessage()));
        }
    }

    /**
     * ESTADÍSTICAS - GET /api/v1/envios/estadisticas
     * Obtiene estadísticas de los envíos
     *
     * @return ResponseEntity con estadísticas
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        try {
            Map<String, Object> estadisticas = envioService.obtenerEstadisticas();
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor: " + e.getMessage()));
        }
    }

    /**
     * INTEGRACIÓN CON SERVICIOS VIA EUREKA
     * ====================================
     */

    /**
     * GET /api/v1/envios/sistemas - Listar envíos de todos los sistemas
     */
    @GetMapping("/sistemas")
    public ResponseEntity<?> listarEnviosDeSistemas() {
        try {
            Map<String, Object> envios = envioService.listarTodosLosEnvios();
            return ResponseEntity.ok(envios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al consultar sistemas: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/v1/envios/{id}/estado/sistema - Actualizar estado en sistema
     */
    @PutMapping("/{id}/estado/sistema")
    public ResponseEntity<?> actualizarEstadoEnSistema(@PathVariable("id") String id,
                                                      @RequestBody Map<String, String> request) {
        try {
            Long envioId = Long.parseLong(id);
            String estado = request.get("estado");

            if (estado == null || estado.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El estado es obligatorio"));
            }

            Map<String, Object> resultado = envioService.actualizarEstadoEnSistema(envioId, estado);

            if (resultado.containsKey("error")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
            }

            return ResponseEntity.ok(resultado);

        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "El ID del envío debe ser numérico"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor: " + e.getMessage()));
        }
    }

    /**
     * GET /api/v1/envios/sistemas/info - Información de todos los sistemas
     */
    @GetMapping("/sistemas/info")
    public ResponseEntity<?> obtenerInfoSistemas() {
        try {
            Map<String, Object> info = envioService.obtenerInfoSistemas();
            return ResponseEntity.ok(info);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener información de sistemas: " + e.getMessage()));
        }
    }

    /**
     * Endpoint adicional para obtener información del servicio (útil para testing)
     * GET /api/v1/envios/info
     */
    @GetMapping("/info")
    public ResponseEntity<?> getServiceInfo() {
        return ResponseEntity.ok(Map.of(
            "servicio", "LogiServices Service Facade",
            "version", "1.0.0",
            "descripcion", "Service Facade que integra TMS, ACMS y SMCS via Eureka",
            "endpoints", new HashMap<String, String>() {{
                put("GET /api/v1/envios", "Listar todos los envíos locales");
                put("GET /api/v1/envios/{id}", "Obtener envío por ID (sistemas + locales)");
                put("GET /api/v1/envios/sistemas", "Listar envíos de todos los sistemas");
                put("POST /api/v1/envios", "Crear nuevo envío local");
                put("PUT /api/v1/envios/{id}", "Actualizar envío local");
                put("PUT /api/v1/envios/{id}/estado/sistema", "Actualizar estado en sistema");
                put("DELETE /api/v1/envios/{id}", "Eliminar envío local");
                put("GET /api/v1/envios/sistema/{sistema}", "Buscar por sistema local");
                put("GET /api/v1/envios/estado/{estado}", "Buscar por estado local");
                put("GET /api/v1/envios/estadisticas", "Obtener estadísticas locales");
                put("GET /api/v1/envios/sistemas/info", "Información de todos los sistemas");
            }},
            "integracion", Map.of(
                "eurekaServer", "http://localhost:8761",
                "serviciosRegistrados", Arrays.asList("TMS", "ACMS", "SMCS"),
                "tecnologia", "Spring Cloud OpenFeign"
            )
        ));
    }
}
