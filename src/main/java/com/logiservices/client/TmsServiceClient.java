package com.logiservices.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Feign Client para comunicación con TMS Service
 *
 * Este cliente permite al Service Facade comunicarse con el TMS Service
 * registrado en Eureka Server de forma transparente.
 *
 * @FeignClient: Especifica el nombre del servicio en Eureka
 * Spring Cloud automáticamente resuelve la URL del servicio
 */
@FeignClient(name = "tms-service")
public interface TmsServiceClient {

    /**
     * GET /api/tms/envios/{id}
     * Consulta un envío específico en TMS
     */
    @GetMapping("/api/tms/envios/{id}")
    Map<String, Object> consultarEnvio(@PathVariable("id") Long id);

    /**
     * GET /api/tms/envios
     * Lista todos los envíos TMS
     */
    @GetMapping("/api/tms/envios")
    Map<String, Object> listarEnvios();

    /**
     * PUT /api/tms/envios/{id}/estado
     * Actualiza el estado de un envío
     */
    @PutMapping("/api/tms/envios/{id}/estado")
    Map<String, Object> actualizarEstado(@PathVariable("id") Long id,
                                        @RequestBody Map<String, String> request);

    /**
     * GET /api/tms/rutas/{origen}/{destino}
     * Consulta rutas terrestres
     */
    @GetMapping("/api/tms/rutas/{origen}/{destino}")
    Map<String, Object> consultarRuta(@PathVariable("origen") String origen,
                                     @PathVariable("destino") String destino);

    /**
     * GET /api/tms/info
     * Información del servicio TMS
     */
    @GetMapping("/api/tms/info")
    Map<String, Object> obtenerInfo();
}

