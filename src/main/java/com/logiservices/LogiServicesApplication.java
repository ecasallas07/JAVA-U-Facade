package com.logiservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de LogiServices Service Facade
 *
 * Esta aplicación funciona como un Service Facade que unifica el acceso
 * a los sistemas TMS (Transport Management System), ACMS (Air Cargo Management System)
 * y SMCS (Sea Management Cargo System).
 *
 * @SpringBootApplication combina:
 * - @Configuration: Marca la clase como fuente de configuración
 * - @EnableAutoConfiguration: Habilita la configuración automática de Spring Boot
 * - @ComponentScan: Escanea el paquete com.logiservices en busca de componentes
 */
@SpringBootApplication
public class LogiServicesApplication {

    /**
     * Método principal que inicia la aplicación Spring Boot
     *
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(LogiServicesApplication.class, args);

        System.out.println("🚀 LogiServices Service Facade iniciado!");
        System.out.println("📱 Endpoint principal: http://localhost:8080/api/v1/envios/{id}");
        System.out.println("🔍 Ejemplo: http://localhost:8080/api/v1/envios/123");
        System.out.println("📊 Info del servicio: http://localhost:8080/api/v1/envios/info");
        System.out.println("");
        System.out.println("📦 Sistemas integrados (simulados):");
        System.out.println("   • TMS - Transport Management System");
        System.out.println("   • ACMS - Air Cargo Management System");
        System.out.println("   • SMCS - Sea Management Cargo System");
    }
}
