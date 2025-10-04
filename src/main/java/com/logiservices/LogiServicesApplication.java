package com.logiservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.cloud.netflix.eureka.EnableEurekaClient; // Deprecated
import org.springframework.cloud.openfeign.EnableFeignClients;

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
// @EnableEurekaClient // Deprecated - Eureka client is auto-configured
@EnableFeignClients
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
        System.out.println("🎯 Registrado en Eureka Server: http://localhost:8761");
        System.out.println("");
        System.out.println("📦 Sistemas integrados (via Eureka):");
        System.out.println("   • TMS Service - http://localhost:8081");
        System.out.println("   • ACMS Service - http://localhost:8082");
        System.out.println("   • SMCS Service - http://localhost:8083");
        System.out.println("");
        System.out.println("🔗 Para ver servicios registrados: http://localhost:8761");
    }
}
