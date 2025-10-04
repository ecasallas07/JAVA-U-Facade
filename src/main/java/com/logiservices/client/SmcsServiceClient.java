package com.logiservices.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Feign Client para comunicación con SMCS Service
 *
 * Este cliente permite al Service Facade comunicarse con el SMCS Service
 * registrado en Eureka Server de forma transparente.
 *
 * @FeignClient: Especifica el nombre del servicio en Eureka
 * Spring Cloud automáticamente resuelve la URL del servicio
 */
@FeignClient(name = "smcs-service")
public interface SmcsServiceClient {

    /**
     * GET /api/smcs/envios/{id}
     * Consulta un envío específico en SMCS
     */
    @GetMapping("/api/smcs/envios/{id}")
    Map<String, Object> consultarEnvio(@PathVariable("id") Long id);

    /**
     * GET /api/smcs/envios
     * Lista todos los envíos SMCS
     */
    @GetMapping("/api/smcs/envios")
    Map<String, Object> listarEnvios();

    /**
     * PUT /api/smcs/envios/{id}/estado
     * Actualiza el estado de un envío
     */
    @PutMapping("/api/smcs/envios/{id}/estado")
    Map<String, Object> actualizarEstado(@PathVariable("id") Long id,
                                        @RequestBody Map<String, String> request);

    /**
     * GET /api/smcs/buques/{origen}/{destino}
     * Consulta buques disponibles
     */
    @GetMapping("/api/smcs/buques/{origen}/{destino}")
    Map<String, Object> consultarBuques(@PathVariable("origen") String origen,
                                       @PathVariable("destino") String destino);

    /**
     * GET /api/smcs/contenedores/{numeroContenedor}
     * Consulta información de contenedor
     */
    @GetMapping("/api/smcs/contenedores/{numeroContenedor}")
    Map<String, Object> consultarContenedor(@PathVariable("numeroContenedor") String numeroContenedor);

    /**
     * GET /api/smcs/info
     * Información del servicio SMCS
     */
    @GetMapping("/api/smcs/info")
    Map<String, Object> obtenerInfo();
}

