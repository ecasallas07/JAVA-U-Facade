package com.logiservices.service;

import com.logiservices.dto.EnvioDto;
import com.logiservices.client.TmsServiceClient;
import com.logiservices.client.AcmsServiceClient;
import com.logiservices.client.SmcsServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Layer para manejo de envíos
 *
 * Esta implementación integra con los servicios TMS, ACMS y SMCS
 * registrados en Eureka Server usando Feign Clients.
 *
 * @Service: Marca la clase como servicio de Spring Boot
 */
@Service
public class EnvioService {

    @Autowired
    private TmsServiceClient tmsServiceClient;

    @Autowired
    private AcmsServiceClient acmsServiceClient;

    @Autowired
    private SmcsServiceClient smcsServiceClient;

    /**
     * Map que mantiene datos de envíos en memoria para operaciones CRUD
     * En producción, esto sería reemplazado por una base de datos
     */
    private final Map<Long, EnvioDto> enviosMock = new HashMap<>();

    /**
     * Constructor que inicializa datos simulados
     * Simulamos datos que en el futuro vendrán de TMS / ACMS / SMCS
     */
    public EnvioService() {
        // Datos simulados de TMS (Transport Management System)
        enviosMock.put(123L, new EnvioDto(123L, "Bogotá", "Medellín", "En tránsito", "TMS"));
        enviosMock.put(124L, new EnvioDto(124L, "Cali", "Pereira", "En tránsito", "TMS"));
        enviosMock.put(125L, new EnvioDto(125L, "Bucaramanga", "Cartagena", "Pendiente", "TMS"));

        // Datos simulados de ACMS (Air Cargo Management System)
        enviosMock.put(456L, new EnvioDto(456L, "Cali", "Cartagena", "Entregado", "ACMS"));
        enviosMock.put(457L, new EnvioDto(457L, "Bogotá", "Miami", "En tránsito", "ACMS"));
        enviosMock.put(458L, new EnvioDto(458L, "Medellín", "Panamá", "Entregado", "ACMS"));

        // Datos simulados de SMCS (Sea Management Cargo System)
        enviosMock.put(789L, new EnvioDto(789L, "Barranquilla", "Buenaventura", "Pendiente", "SMCS"));
        enviosMock.put(790L, new EnvioDto(790L, "Cartagena", "Valencia", "En tránsito", "SMCS"));
        enviosMock.put(791L, new EnvioDto(791L, "Buenaventura", "Shanghai", "Entregado", "SMCS"));
    }

    /**
     * Obtiene un envío por su ID
     *
     * @param id ID del envío a buscar
     * @return EnvioDto con la información del envío, o null si no existe
     */
    public EnvioDto getEnvioById(Long id) {
        return enviosMock.get(id);
    }

    /**
     * Obtiene todos los envíos disponibles (método adicional para testing)
     *
     * @return Map con todos los envíos
     */
    public Map<Long, EnvioDto> getAllEnvios() {
        return new HashMap<>(enviosMock);
    }

    /**
     * Verifica si existe un envío con el ID dado
     *
     * @param id ID del envío
     * @return true si existe, false en caso contrario
     */
    public boolean existsEnvio(Long id) {
        return enviosMock.containsKey(id);
    }

    /**
     * CREAR - Crea un nuevo envío
     *
     * @param envioDto Datos del envío a crear (sin ID)
     * @return EnvioDto creado con ID asignado
     * @throws RuntimeException si el envío ya existe
     */
    public EnvioDto crearEnvio(EnvioDto envioDto) {
        // Generar un nuevo ID único
        Long nuevoId = generarNuevoId();

        // Verificar que no exista un envío con ese ID (por seguridad)
        if (enviosMock.containsKey(nuevoId)) {
            throw new RuntimeException("Error interno: ID duplicado generado");
        }

        // Crear el nuevo envío con el ID generado
        EnvioDto nuevoEnvio = new EnvioDto(
            nuevoId,
            envioDto.getOrigen(),
            envioDto.getDestino(),
            envioDto.getEstado(),
            envioDto.getSistemaOrigen()
        );

        // Guardar en el map
        enviosMock.put(nuevoId, nuevoEnvio);

        return nuevoEnvio;
    }

    /**
     * ACTUALIZAR - Actualiza un envío existente
     *
     * @param id ID del envío a actualizar
     * @param envioDto Datos nuevos del envío
     * @return EnvioDto actualizado
     * @throws RuntimeException si el envío no existe
     */
    public EnvioDto actualizarEnvio(Long id, EnvioDto envioDto) {
        // Verificar que el envío exista
        if (!enviosMock.containsKey(id)) {
            throw new RuntimeException("No se encontró el envío con ID " + id);
        }

        // Crear el envío actualizado con el mismo ID
        EnvioDto envioActualizado = new EnvioDto(
            id, // Mantener el ID original
            envioDto.getOrigen(),
            envioDto.getDestino(),
            envioDto.getEstado(),
            envioDto.getSistemaOrigen()
        );

        // Actualizar en el map
        enviosMock.put(id, envioActualizado);

        return envioActualizado;
    }

    /**
     * ELIMINAR - Elimina un envío
     *
     * @param id ID del envío a eliminar
     * @return EnvioDto eliminado
     * @throws RuntimeException si el envío no existe
     */
    public EnvioDto eliminarEnvio(Long id) {
        // Verificar que el envío exista
        if (!enviosMock.containsKey(id)) {
            throw new RuntimeException("No se encontró el envío con ID " + id);
        }

        // Obtener el envío antes de eliminarlo
        EnvioDto envioEliminado = enviosMock.get(id);

        // Eliminar del map
        enviosMock.remove(id);

        return envioEliminado;
    }

    /**
     * Obtiene todos los envíos como Lista (más útil para APIs)
     *
     * @return Lista de todos los envíos
     */
    public List<EnvioDto> getAllEnviosList() {
        return new ArrayList<>(enviosMock.values());
    }

    /**
     * Busca envíos por sistema de origen
     *
     * @param sistemaOrigen Sistema a buscar (TMS, ACMS, SMCS)
     * @return Lista de envíos del sistema especificado
     */
    public List<EnvioDto> buscarPorSistemaOrigen(String sistemaOrigen) {
        return enviosMock.values().stream()
                .filter(envio -> envio.getSistemaOrigen().equalsIgnoreCase(sistemaOrigen))
                .collect(Collectors.toList());
    }

    /**
     * Busca envíos por estado
     *
     * @param estado Estado a buscar
     * @return Lista de envíos con el estado especificado
     */
    public List<EnvioDto> buscarPorEstado(String estado) {
        return enviosMock.values().stream()
                .filter(envio -> envio.getEstado().equalsIgnoreCase(estado))
                .collect(Collectors.toList());
    }

    /**
     * Genera un nuevo ID único para envíos
     * En un sistema real, esto sería manejado por la base de datos
     *
     * @return Nuevo ID único
     */
    private Long generarNuevoId() {
        // Obtener el ID más alto actual
        Long maxId = enviosMock.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);

        // Devolver el siguiente ID
        return maxId + 1;
    }

    /**
     * Obtiene estadísticas de los envíos
     *
     * @return Map con estadísticas
     */
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();

        // Total de envíos
        stats.put("totalEnvios", enviosMock.size());

        // Conteo por sistema
        Map<String, Long> porSistema = enviosMock.values().stream()
                .collect(Collectors.groupingBy(
                    EnvioDto::getSistemaOrigen,
                    Collectors.counting()
                ));
        stats.put("porSistema", porSistema);

        // Conteo por estado
        Map<String, Long> porEstado = enviosMock.values().stream()
                .collect(Collectors.groupingBy(
                    EnvioDto::getEstado,
                    Collectors.counting()
                ));
        stats.put("porEstado", porEstado);

        return stats;
    }

    /**
     * INTEGRACIÓN CON SERVICIOS REALES VIA EUREKA
     * ===========================================
     */

    /**
     * Consulta un envío en el sistema correspondiente (TMS, ACMS, SMCS)
     *
     * @param id ID del envío
     * @return Información del envío desde el sistema correspondiente
     */
    public Map<String, Object> consultarEnvioEnSistema(Long id) {
        try {
            // Intentar consultar en TMS
            Map<String, Object> resultado = tmsServiceClient.consultarEnvio(id);
            if (resultado != null && !resultado.containsKey("error")) {
                return resultado;
            }
        } catch (Exception e) {
            // TMS no disponible o envío no encontrado, continuar con ACMS
        }

        try {
            // Intentar consultar en ACMS
            Map<String, Object> resultado = acmsServiceClient.consultarEnvio(id);
            if (resultado != null && !resultado.containsKey("error")) {
                return resultado;
            }
        } catch (Exception e) {
            // ACMS no disponible o envío no encontrado, continuar con SMCS
        }

        try {
            // Intentar consultar en SMCS
            Map<String, Object> resultado = smcsServiceClient.consultarEnvio(id);
            if (resultado != null && !resultado.containsKey("error")) {
                return resultado;
            }
        } catch (Exception e) {
            // SMCS no disponible o envío no encontrado
        }

        // Si no se encuentra en ningún sistema
        return Map.of("error", "Envío no encontrado en ningún sistema", "id", id);
    }

    /**
     * Lista todos los envíos de todos los sistemas
     *
     * @return Lista consolidada de envíos de todos los sistemas
     */
    public Map<String, Object> listarTodosLosEnvios() {
        List<Map<String, Object>> todosLosEnvios = new ArrayList<>();

        try {
            // Obtener envíos de TMS
            Map<String, Object> enviosTms = tmsServiceClient.listarEnvios();
            if (enviosTms != null && enviosTms.containsKey("envios")) {
                List<Map<String, Object>> envios = (List<Map<String, Object>>) enviosTms.get("envios");
                todosLosEnvios.addAll(envios);
            }
        } catch (Exception e) {
            // TMS no disponible
        }

        try {
            // Obtener envíos de ACMS
            Map<String, Object> enviosAcms = acmsServiceClient.listarEnvios();
            if (enviosAcms != null && enviosAcms.containsKey("envios")) {
                List<Map<String, Object>> envios = (List<Map<String, Object>>) enviosAcms.get("envios");
                todosLosEnvios.addAll(envios);
            }
        } catch (Exception e) {
            // ACMS no disponible
        }

        try {
            // Obtener envíos de SMCS
            Map<String, Object> enviosSmcs = smcsServiceClient.listarEnvios();
            if (enviosSmcs != null && enviosSmcs.containsKey("envios")) {
                List<Map<String, Object>> envios = (List<Map<String, Object>>) enviosSmcs.get("envios");
                todosLosEnvios.addAll(envios);
            }
        } catch (Exception e) {
            // SMCS no disponible
        }

        return Map.of(
            "total", todosLosEnvios.size(),
            "envios", todosLosEnvios,
            "sistemas", Arrays.asList("TMS", "ACMS", "SMCS")
        );
    }

    /**
     * Actualiza el estado de un envío en el sistema correspondiente
     *
     * @param id ID del envío
     * @param estado Nuevo estado
     * @return Resultado de la actualización
     */
    public Map<String, Object> actualizarEstadoEnSistema(Long id, String estado) {
        Map<String, String> request = Map.of("estado", estado);

        try {
            // Intentar actualizar en TMS
            Map<String, Object> resultado = tmsServiceClient.actualizarEstado(id, request);
            if (resultado != null && !resultado.containsKey("error")) {
                return resultado;
            }
        } catch (Exception e) {
            // TMS no disponible o envío no encontrado, continuar con ACMS
        }

        try {
            // Intentar actualizar en ACMS
            Map<String, Object> resultado = acmsServiceClient.actualizarEstado(id, request);
            if (resultado != null && !resultado.containsKey("error")) {
                return resultado;
            }
        } catch (Exception e) {
            // ACMS no disponible o envío no encontrado, continuar con SMCS
        }

        try {
            // Intentar actualizar en SMCS
            Map<String, Object> resultado = smcsServiceClient.actualizarEstado(id, request);
            if (resultado != null && !resultado.containsKey("error")) {
                return resultado;
            }
        } catch (Exception e) {
            // SMCS no disponible o envío no encontrado
        }

        return Map.of("error", "Envío no encontrado en ningún sistema para actualizar", "id", id);
    }

    /**
     * Obtiene información de todos los sistemas registrados
     *
     * @return Información consolidada de todos los sistemas
     */
    public Map<String, Object> obtenerInfoSistemas() {
        Map<String, Object> infoSistemas = new HashMap<>();

        try {
            Map<String, Object> infoTms = tmsServiceClient.obtenerInfo();
            infoSistemas.put("TMS", infoTms);
        } catch (Exception e) {
            infoSistemas.put("TMS", Map.of("error", "Servicio no disponible"));
        }

        try {
            Map<String, Object> infoAcms = acmsServiceClient.obtenerInfo();
            infoSistemas.put("ACMS", infoAcms);
        } catch (Exception e) {
            infoSistemas.put("ACMS", Map.of("error", "Servicio no disponible"));
        }

        try {
            Map<String, Object> infoSmcs = smcsServiceClient.obtenerInfo();
            infoSistemas.put("SMCS", infoSmcs);
        } catch (Exception e) {
            infoSistemas.put("SMCS", Map.of("error", "Servicio no disponible"));
        }

        return Map.of(
            "serviceFacade", "LogiServices Service Facade",
            "version", "1.0.0",
            "descripcion", "Integración unificada de sistemas TMS, ACMS y SMCS",
            "sistemas", infoSistemas,
            "eurekaServer", "http://localhost:8761"
        );
    }
}
