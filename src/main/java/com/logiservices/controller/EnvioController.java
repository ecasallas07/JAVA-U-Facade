package com.logiservices.controller;

import com.logiservices.dto.EnvioDto;
import com.logiservices.service.EnvioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> getEnvioById(@PathVariable("id") String id) {
        try {
            // Convertir el ID de String a Long
            Long envioId = Long.parseLong(id);

            // Buscar el envío en el servicio
            EnvioDto envio = envioService.getEnvioById(envioId);

            // Si no se encuentra el envío, devolver 404
            if (envio == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontró información del envío con ID " + envioId);
            }

            // Si se encuentra, devolver 200 OK con los datos
            return ResponseEntity.ok(envio);

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
     * Endpoint adicional para obtener información del servicio (útil para testing)
     * GET /api/v1/envios/info
     */
    @GetMapping("/info")
    public ResponseEntity<?> getServiceInfo() {
        return ResponseEntity.ok(Map.of(
            "servicio", "LogiServices Service Facade",
            "version", "1.0.0",
            "endpoints", Map.of(
                "GET /api/v1/envios", "Listar todos los envíos",
                "GET /api/v1/envios/{id}", "Obtener envío por ID",
                "POST /api/v1/envios", "Crear nuevo envío",
                "PUT /api/v1/envios/{id}", "Actualizar envío",
                "DELETE /api/v1/envios/{id}", "Eliminar envío",
                "GET /api/v1/envios/sistema/{sistema}", "Buscar por sistema",
                "GET /api/v1/envios/estado/{estado}", "Buscar por estado",
                "GET /api/v1/envios/estadisticas", "Obtener estadísticas"
            )
        ));
    }
}
