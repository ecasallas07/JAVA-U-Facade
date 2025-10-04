package com.logiservices.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Feign Client para comunicación con ACMS Service
 *
 * Este cliente permite al Service Facade comunicarse con el ACMS Service
 * registrado en Eureka Server de forma transparente.
 *
 * @FeignClient: Especifica el nombre del servicio en Eureka
 * Spring Cloud automáticamente resuelve la URL del servicio
 */
@FeignClient(name = "acms-service")
public interface AcmsServiceClient {

    /**
     * GET /api/acms/envios/{id}
     * Consulta un envío específico en ACMS
     */
    @GetMapping("/api/acms/envios/{id}")
    Map<String, Object> consultarEnvio(@PathVariable("id") Long id);

    /**
     * GET /api/acms/envios
     * Lista todos los envíos ACMS
     */
    @GetMapping("/api/acms/envios")
    Map<String, Object> listarEnvios();

    /**
     * PUT /api/acms/envios/{id}/estado
     * Actualiza el estado de un envío
     */
    @PutMapping("/api/acms/envios/{id}/estado")
    Map<String, Object> actualizarEstado(@PathVariable("id") Long id,
                                        @RequestBody Map<String, String> request);

    /**
     * GET /api/acms/vuelos/{origen}/{destino}
     * Consulta vuelos disponibles
     */
    @GetMapping("/api/acms/vuelos/{origen}/{destino}")
    Map<String, Object> consultarVuelos(@PathVariable("origen") String origen,
                                       @PathVariable("destino") String destino);

    /**
     * GET /api/acms/info
     * Información del servicio ACMS
     */
    @GetMapping("/api/acms/info")
    Map<String, Object> obtenerInfo();
}

