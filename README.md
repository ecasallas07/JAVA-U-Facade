# LogiServices Service Facade

## 🎯 Objetivo

Desarrollar el endpoint principal `/api/v1/envios/{id}` en Spring Boot, que funcione como punto de entrada unificado del Service Facade, devolviendo la información de un envío desde los sistemas TMS, ACMS y SMCS.

## 🏗️ Arquitectura Técnica

```
[ Cliente / Postman ]
        ↓
[ Controller Layer ]
        ↓
[ Service Layer ]
        ↓
[ Integration Layer (mock por ahora) ]
        ↓
[ Datos simulados de TMS, ACMS, SMCS ]
```

## 📦 Estructura del Proyecto

```
src/
└── main/
    └── java/
        └── com.logiservices/
            ├── controller/
            │   └── EnvioController.java
            ├── service/
            │   └── EnvioService.java
            ├── dto/
            │   └── EnvioDto.java
            └── LogiServicesApplication.java
```

## 🚀 Cómo Ejecutar

### Prerrequisitos
- Java 17 o superior
- Maven 3.6 o superior

### Pasos para ejecutar:

1. **Clonar/descargar el proyecto**

2. **Navegar al directorio del proyecto:**
   ```bash
   cd /Users/ecasallas/Documents/Java-U
   ```

3. **Ejecutar la aplicación:**
   ```bash
   mvn spring-boot:run
   ```

4. **Verificar que esté funcionando:**
   - Abrir navegador en: http://localhost:8080/api/v1/envios/info
   - O usar Postman con los requests del archivo `POSTMAN_COLLECTION.md`

## 🧪 Testing con Postman

### Casos de Prueba Principales:

#### ✅ Casos Exitosos:
- `GET /api/v1/envios/123` → Envío TMS
- `GET /api/v1/envios/456` → Envío ACMS  
- `GET /api/v1/envios/789` → Envío SMCS

#### ❌ Casos de Error:
- `GET /api/v1/envios/999` → 404 Not Found
- `GET /api/v1/envios/abc` → 400 Bad Request

Ver archivo `POSTMAN_COLLECTION.md` para detalles completos.

## 🔧 Configuración

### application.properties
```properties
spring.application.name=logiservices-service-facade
server.port=8080
```

### Dependencias principales:
- `spring-boot-starter-web`: Servidor web y REST APIs
- `spring-boot-starter-test`: Testing
- `spring-boot-devtools`: Desarrollo (recarga automática)

## 📊 Datos Simulados

### TMS (Transport Management System):
- ID: 123, 124, 125
- Estados: "En tránsito", "Pendiente"

### ACMS (Air Cargo Management System):
- ID: 456, 457, 458
- Estados: "Entregado", "En tránsito"

### SMCS (Sea Management Cargo System):
- ID: 789, 790, 791
- Estados: "Pendiente", "En tránsito", "Entregado"

## 🔮 Próximas Entregas

- Integración real con servicios TMS, ACMS, SMCS
- Registro en Eureka Service Registry
- Implementación de circuit breakers
- Logging y monitoreo
- Documentación con Swagger/OpenAPI

## 📝 Notas de Desarrollo

- **Service Facade Pattern**: Unifica el acceso a múltiples sistemas
- **Arquitectura en Capas**: Controller → Service → Integration
- **Datos Mock**: Simulación de integración con servicios externos
- **Manejo de Errores**: Respuestas HTTP apropiadas (404, 400)
- **RESTful API**: Endpoint estándar con versionado `/api/v1/`

## 🎥 Video Demostrativo

Para el video de 1.5 minutos, mostrar:
1. Aplicación ejecutándose
2. Casos exitosos con Postman
3. Casos de error
4. Arquitectura implementada
