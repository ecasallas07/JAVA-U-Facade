# Diagramas de Secuencia UML - LogiServices

## 1. Diagrama de Autenticación JWT

```mermaid
sequenceDiagram
    participant Cliente
    participant AuthController
    participant AuthenticationManager
    participant UsuarioService
    participant JwtUtil
    participant SecurityContext

    Cliente->>AuthController: POST /api/v1/auth/login
    Note over Cliente,AuthController: {username: "admin", password: "admin123"}
    
    AuthController->>AuthenticationManager: authenticate(username, password)
    AuthenticationManager->>UsuarioService: loadUserByUsername(username)
    UsuarioService-->>AuthenticationManager: UserDetails
    AuthenticationManager-->>AuthController: Authentication
    
    AuthController->>SecurityContext: setAuthentication(auth)
    AuthController->>JwtUtil: generateToken(userDetails)
    JwtUtil-->>AuthController: JWT Token
    
    AuthController->>UsuarioService: actualizarUltimoAcceso(username)
    AuthController-->>Cliente: 200 OK + JWT Response
    Note over Cliente,AuthController: {token: "eyJ...", type: "Bearer", username: "admin", roles: ["ADMIN"]}
```

## 2. Diagrama de Consulta de Envío con Autenticación

```mermaid
sequenceDiagram
    participant Cliente
    participant JwtAuthenticationFilter
    participant SecurityContext
    participant EnvioController
    participant EnvioService
    participant TmsServiceClient
    participant AcmsServiceClient
    participant SmcsServiceClient

    Cliente->>JwtAuthenticationFilter: GET /api/v1/envios/123
    Note over Cliente,JwtAuthenticationFilter: Authorization: Bearer eyJ...
    
    JwtAuthenticationFilter->>JwtAuthenticationFilter: validateToken(jwt)
    JwtAuthenticationFilter->>SecurityContext: setAuthentication()
    JwtAuthenticationFilter->>EnvioController: Request
    
    EnvioController->>SecurityContext: check authentication
    SecurityContext-->>EnvioController: User authenticated
    
    EnvioController->>EnvioService: consultarEnvioEnSistema(123)
    
    par Consulta en paralelo
        EnvioService->>TmsServiceClient: getEnvio(123)
        TmsServiceClient-->>EnvioService: Response TMS
    and
        EnvioService->>AcmsServiceClient: getEnvio(123)
        AcmsServiceClient-->>EnvioService: Response ACMS
    and
        EnvioService->>SmcsServiceClient: getEnvio(123)
        SmcsServiceClient-->>EnvioService: Response SMCS
    end
    
    EnvioService-->>EnvioController: EnvioDto
    EnvioController-->>Cliente: 200 OK + Envio Data
```

## 3. Diagrama de Creación de Envío (Solo ADMIN/OPERADOR)

```mermaid
sequenceDiagram
    participant Cliente
    participant JwtAuthenticationFilter
    participant SecurityContext
    participant EnvioController
    participant EnvioService
    participant UsuarioRepository

    Cliente->>JwtAuthenticationFilter: POST /api/v1/envios
    Note over Cliente,JwtAuthenticationFilter: Authorization: Bearer eyJ...<br/>{origen: "Madrid", destino: "Barcelona", estado: "Pendiente"}
    
    JwtAuthenticationFilter->>JwtAuthenticationFilter: validateToken(jwt)
    JwtAuthenticationFilter->>SecurityContext: setAuthentication()
    JwtAuthenticationFilter->>EnvioController: Request
    
    EnvioController->>SecurityContext: check authentication
    SecurityContext-->>EnvioController: User authenticated
    
    EnvioController->>SecurityContext: check @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    SecurityContext->>UsuarioRepository: getUserRoles()
    UsuarioRepository-->>SecurityContext: User roles
    SecurityContext-->>EnvioController: Authorization granted
    
    EnvioController->>EnvioController: validate request data
    EnvioController->>EnvioService: crearEnvio(envioDto)
    EnvioService-->>EnvioController: EnvioDto created
    
    EnvioController-->>Cliente: 201 Created + Envio Data
```

## 4. Diagrama de Error de Autenticación

```mermaid
sequenceDiagram
    participant Cliente
    participant JwtAuthenticationFilter
    participant EnvioController
    participant GlobalExceptionHandler

    Cliente->>JwtAuthenticationFilter: GET /api/v1/envios/123
    Note over Cliente,JwtAuthenticationFilter: Sin Authorization header
    
    JwtAuthenticationFilter->>JwtAuthenticationFilter: getJwtFromRequest()
    JwtAuthenticationFilter-->>JwtAuthenticationFilter: null (no token)
    JwtAuthenticationFilter->>EnvioController: Request (no auth)
    
    EnvioController->>EnvioController: check authentication
    EnvioController-->>GlobalExceptionHandler: AccessDeniedException
    
    GlobalExceptionHandler->>GlobalExceptionHandler: handleAccessDeniedException()
    GlobalExceptionHandler-->>Cliente: 403 Forbidden
    Note over Cliente,GlobalExceptionHandler: {code: "ACCESS_DENIED", message: "Acceso denegado"}
```

## 5. Diagrama de Validación de Token

```mermaid
sequenceDiagram
    participant Cliente
    participant AuthController
    participant JwtUtil
    participant UsuarioService

    Cliente->>AuthController: POST /api/v1/auth/validate
    Note over Cliente,AuthController: {token: "eyJ..."}
    
    AuthController->>JwtUtil: validateToken(token)
    JwtUtil->>JwtUtil: parseClaimsJws(token)
    JwtUtil->>JwtUtil: check expiration
    JwtUtil-->>AuthController: true (valid)
    
    AuthController->>JwtUtil: extractUsername(token)
    JwtUtil-->>AuthController: username
    
    alt Token válido
        AuthController-->>Cliente: 200 OK
        Note over Cliente,AuthController: {valid: true, username: "admin", message: "Token válido"}
    else Token inválido/expirado
        AuthController-->>Cliente: 401 Unauthorized
        Note over Cliente,AuthController: {valid: false, message: "Token inválido o expirado"}
    end
```

## 6. Diagrama de Integración con Servicios Externos

```mermaid
sequenceDiagram
    participant Cliente
    participant EnvioController
    participant EnvioService
    participant EurekaClient
    participant TmsService
    participant AcmsService
    participant SmcsService

    Cliente->>EnvioController: GET /api/v1/envios/sistemas
    Note over Cliente,EnvioController: Authorization: Bearer eyJ...
    
    EnvioController->>EnvioService: listarTodosLosEnvios()
    
    EnvioService->>EurekaClient: discover services
    EurekaClient-->>EnvioService: Service instances
    
    par Consulta en paralelo
        EnvioService->>TmsService: GET /api/tms/envios
        TmsService-->>EnvioService: List<EnvioDto>
    and
        EnvioService->>AcmsService: GET /api/acms/envios
        AcmsService-->>EnvioService: List<EnvioDto>
    and
        EnvioService->>SmcsService: GET /api/smcs/envios
        SmcsService-->>EnvioService: List<EnvioDto>
    end
    
    EnvioService->>EnvioService: merge results
    EnvioService-->>EnvioController: Map<String, List<EnvioDto>>
    EnvioController-->>Cliente: 200 OK + Consolidated Data
```

## 7. Diagrama de Manejo de Excepciones

```mermaid
sequenceDiagram
    participant Cliente
    participant EnvioController
    participant EnvioService
    participant GlobalExceptionHandler

    Cliente->>EnvioController: GET /api/v1/envios/999
    Note over Cliente,EnvioController: Authorization: Bearer eyJ...
    
    EnvioController->>EnvioService: getEnvioById(999)
    EnvioService->>EnvioService: search in TMS, ACMS, SMCS
    EnvioService->>EnvioService: search in local database
    EnvioService-->>EnvioController: null (not found)
    
    EnvioController-->>GlobalExceptionHandler: ResourceNotFoundException
    
    GlobalExceptionHandler->>GlobalExceptionHandler: handleResourceNotFoundException()
    GlobalExceptionHandler-->>Cliente: 404 Not Found
    Note over Cliente,GlobalExceptionHandler: {code: "RESOURCE_NOT_FOUND", message: "Envío no encontrado con ID: 999"}
```

## 8. Diagrama de Roles y Permisos

```mermaid
sequenceDiagram
    participant Cliente
    participant SecurityFilterChain
    participant SecurityContext
    participant EnvioController

    Note over Cliente,EnvioController: Diferentes roles y sus permisos

    rect rgb(255, 200, 200)
        Note over Cliente,EnvioController: ADMIN - Acceso completo
        Cliente->>SecurityFilterChain: Request con rol ADMIN
        SecurityFilterChain->>SecurityContext: check role ADMIN
        SecurityContext-->>SecurityFilterChain: Access granted
        SecurityFilterChain->>EnvioController: All operations allowed
    end

    rect rgb(200, 255, 200)
        Note over Cliente,EnvioController: OPERADOR - CRUD operations
        Cliente->>SecurityFilterChain: Request con rol OPERADOR
        SecurityFilterChain->>SecurityContext: check role OPERADOR
        SecurityContext-->>SecurityFilterChain: Access granted
        SecurityFilterChain->>EnvioController: CRUD operations allowed
    end

    rect rgb(200, 200, 255)
        Note over Cliente,EnvioController: CONSULTOR - Solo lectura
        Cliente->>SecurityFilterChain: Request con rol CONSULTOR
        SecurityFilterChain->>SecurityContext: check role CONSULTOR
        SecurityContext-->>SecurityFilterChain: Read access granted
        SecurityFilterChain->>EnvioController: Only GET operations allowed
    end

    rect rgb(255, 255, 200)
        Note over Cliente,EnvioController: CLIENTE - Acceso limitado
        Cliente->>SecurityFilterChain: Request con rol CLIENTE
        SecurityFilterChain->>SecurityContext: check role CLIENTE
        SecurityContext-->>SecurityFilterChain: Limited access granted
        SecurityFilterChain->>EnvioController: Limited GET operations allowed
    end
```

## Resumen de Códigos de Error

| Código | Descripción | Cuándo ocurre |
|--------|-------------|---------------|
| 200 | OK | Operación exitosa |
| 201 | Created | Recurso creado exitosamente |
| 400 | Bad Request | Datos de entrada inválidos |
| 401 | Unauthorized | No autenticado o token inválido |
| 403 | Forbidden | Sin permisos para la operación |
| 404 | Not Found | Recurso no encontrado |
| 500 | Internal Server Error | Error interno del servidor |
| 503 | Service Unavailable | Error de integración con servicios externos |

## Flujo de Autenticación Resumido

1. **Login**: Cliente envía credenciales → Recibe JWT token
2. **Request**: Cliente incluye token en header Authorization
3. **Validation**: Filtro JWT valida token y establece autenticación
4. **Authorization**: Spring Security verifica permisos del usuario
5. **Response**: Operación ejecutada o error devuelto
