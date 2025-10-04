package com.logiservices.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger
 *
 * Define la documentación de la API REST con información detallada
 * sobre endpoints, autenticación y respuestas
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LogiServices Service Facade API")
                        .description("""
                                ## 🚀 LogiServices Service Facade

                                **Service Facade que unifica el acceso a los sistemas de logística:**
                                - **TMS** (Transport Management System)
                                - **ACMS** (Air Cargo Management System)
                                - **SMCS** (Sea Management Cargo System)

                                ### 🔐 Autenticación

                                Esta API utiliza **JWT (JSON Web Tokens)** para autenticación:

                                1. **Obtener token**: `POST /api/v1/auth/login`
                                2. **Usar token**: Incluir en header `Authorization: Bearer <token>`

                                ### 👥 Usuarios de Prueba

                                | Usuario | Contraseña | Rol | Descripción |
                                |---------|------------|-----|-------------|
                                | admin | admin123 | ADMIN | Acceso completo |
                                | operador | operador123 | OPERADOR | Consultar y actualizar |
                                | consultor | consultor123 | CONSULTOR | Solo consultar |
                                | cliente | cliente123 | CLIENTE | Acceso limitado |

                                ### 📋 Códigos de Error

                                | Código | Descripción |
                                |--------|-------------|
                                | 200 | OK - Operación exitosa |
                                | 201 | Created - Recurso creado |
                                | 400 | Bad Request - Datos inválidos |
                                | 401 | Unauthorized - No autenticado |
                                | 403 | Forbidden - Sin permisos |
                                | 404 | Not Found - Recurso no encontrado |
                                | 500 | Internal Server Error - Error del servidor |

                                ### 🔗 Integración con Eureka

                                - **Eureka Server**: http://localhost:8761
                                - **Servicios registrados**: TMS, ACMS, SMCS
                                - **Tecnología**: Spring Cloud OpenFeign

                                ### 🎯 Endpoints Principales

                                - `GET /api/v1/envios/{id}` - Consultar envío por ID
                                - `GET /api/v1/envios/sistemas` - Listar envíos de todos los sistemas
                                - `POST /api/v1/envios` - Crear nuevo envío
                                - `PUT /api/v1/envios/{id}` - Actualizar envío
                                - `DELETE /api/v1/envios/{id}` - Eliminar envío
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("LogiServices Team")
                                .email("support@logiservices.com")
                                .url("https://logiservices.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de desarrollo"),
                        new Server()
                                .url("https://api.logiservices.com")
                                .description("Servidor de producción")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("""
                                        ## 🔑 Autenticación JWT

                                        Para usar esta API, necesitas autenticarte:

                                        1. **Obtener token**:
                                           ```bash
                                           POST /api/v1/auth/login
                                           {
                                             "username": "admin",
                                             "password": "admin123"
                                           }
                                           ```

                                        2. **Usar token**:
                                           ```bash
                                           Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
                                           ```

                                        3. **Validar token**:
                                           ```bash
                                           POST /api/v1/auth/validate
                                           {
                                             "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                                           }
                                           ```
                                        """)));
    }
}
