# 🎥 Guía para Demostración de Autenticación (1.5 min)

## 📋 Script para el Video

### 🎬 Introducción (0-15 segundos)
> "Hola, en este video voy a demostrar cómo funciona la autenticación JWT en el sistema LogiServices. Veremos el acceso sin token vs con token válido."

### 🔧 Preparación (15-30 segundos)
> "Primero, voy a iniciar la aplicación y mostrar los endpoints disponibles."

**Acciones:**
1. Ejecutar `mvn spring-boot:run`
2. Mostrar en navegador: `http://localhost:8080/swagger-ui.html`
3. Mostrar la documentación de la API

### ❌ Demostración SIN Token (30-60 segundos)
> "Ahora voy a intentar acceder a un endpoint protegido sin token para mostrar el error de autenticación."

**Acciones:**
1. Abrir Postman o usar curl
2. Hacer GET a `http://localhost:8080/api/v1/envios/123` SIN Authorization header
3. Mostrar respuesta 403 Forbidden
4. Explicar: "Como vemos, sin token no podemos acceder a los endpoints protegidos"

### ✅ Demostración CON Token (60-90 segundos)
> "Ahora voy a obtener un token JWT y usarlo para acceder al mismo endpoint."

**Acciones:**
1. POST a `http://localhost:8080/api/v1/auth/login`
   ```json
   {
     "username": "admin",
     "password": "admin123"
   }
   ```
2. Copiar el token de la respuesta
3. Hacer GET a `http://localhost:8080/api/v1/envios/123` CON Authorization header
4. Mostrar respuesta 200 OK con datos del envío
5. Explicar: "Con el token JWT válido, ahora sí podemos acceder a los datos"

### 🎯 Conclusión (90-90 segundos)
> "Como vemos, la autenticación JWT protege nuestros endpoints y permite acceso controlado a los datos del sistema LogiServices."

## 🛠️ Comandos para la Demostración

### 1. Iniciar la aplicación
```bash
cd /Users/ecasallas/Documents/Java-U
mvn spring-boot:run
```

### 2. Acceso SIN token (debe fallar)
```bash
curl -X GET http://localhost:8080/api/v1/envios/123
```

**Respuesta esperada:**
```json
{
  "code": "ACCESS_DENIED",
  "message": "Acceso denegado",
  "status": 403,
  "timestamp": "2024-01-15T10:30:00",
  "path": "/api/v1/envios/123"
}
```

### 3. Obtener token JWT
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Respuesta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "admin",
  "email": "admin@logiservices.com",
  "nombreCompleto": "Administrador del Sistema",
  "roles": ["ADMIN"],
  "expiresAt": "2024-01-16T10:30:00"
}
```

### 4. Acceso CON token (debe funcionar)
```bash
curl -X GET http://localhost:8080/api/v1/envios/123 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Respuesta esperada:**
```json
{
  "id": 123,
  "origen": "Madrid",
  "destino": "Barcelona",
  "estado": "En tránsito",
  "sistemaOrigen": "TMS",
  "fechaCreacion": "2024-01-15T09:00:00",
  "fechaActualizacion": "2024-01-15T10:00:00"
}
```

## 📱 Usando Postman

### 1. Crear colección "LogiServices Auth Demo"

### 2. Request: Login
- **Method**: POST
- **URL**: `http://localhost:8080/api/v1/auth/login`
- **Headers**: `Content-Type: application/json`
- **Body** (raw JSON):
```json
{
  "username": "admin",
  "password": "admin123"
}
```

### 3. Request: Get Envío (Sin Token)
- **Method**: GET
- **URL**: `http://localhost:8080/api/v1/envios/123`
- **Headers**: (sin Authorization)

### 4. Request: Get Envío (Con Token)
- **Method**: GET
- **URL**: `http://localhost:8080/api/v1/envios/123`
- **Headers**: `Authorization: Bearer {{token}}`
- **Variables**: Crear variable `token` con el valor del JWT

## 🎭 Diferentes Roles para Demostrar

### Admin (Acceso completo)
```json
{
  "username": "admin",
  "password": "admin123"
}
```

### Operador (CRUD operations)
```json
{
  "username": "operador",
  "password": "operador123"
}
```

### Consultor (Solo lectura)
```json
{
  "username": "consultor",
  "password": "consultor123"
}
```

### Cliente (Acceso limitado)
```json
{
  "username": "cliente",
  "password": "cliente123"
}
```

## 🔍 Endpoints para Demostrar

### Públicos (sin autenticación)
- `GET /api/v1/auth/info` - Información de autenticación
- `GET /api/v1/envios/info` - Información del servicio
- `GET /swagger-ui.html` - Documentación Swagger

### Protegidos (requieren autenticación)
- `GET /api/v1/envios/{id}` - Consultar envío
- `GET /api/v1/envios` - Listar envíos
- `POST /api/v1/envios` - Crear envío (ADMIN/OPERADOR)
- `PUT /api/v1/envios/{id}` - Actualizar envío (ADMIN/OPERADOR)
- `DELETE /api/v1/envios/{id}` - Eliminar envío (solo ADMIN)

## 📊 Casos de Error para Mostrar

### 1. Token inválido
```bash
curl -X GET http://localhost:8080/api/v1/envios/123 \
  -H "Authorization: Bearer token_invalido"
```

### 2. Token expirado
```bash
# Usar un token que haya expirado
curl -X GET http://localhost:8080/api/v1/envios/123 \
  -H "Authorization: Bearer token_expirado"
```

### 3. Sin permisos (rol incorrecto)
```bash
# Login como CONSULTOR e intentar crear envío
curl -X POST http://localhost:8080/api/v1/envios \
  -H "Authorization: Bearer token_consultor" \
  -H "Content-Type: application/json" \
  -d '{"origen": "Madrid", "destino": "Barcelona"}'
```

## 🎬 Puntos Clave para el Video

1. **Mostrar la diferencia clara** entre acceso sin token vs con token
2. **Explicar el flujo JWT** de manera simple
3. **Demostrar diferentes roles** y sus permisos
4. **Mostrar la documentación Swagger** como referencia
5. **Incluir casos de error** para mostrar robustez

## 📝 Notas para la Grabación

- **Duración total**: 1.5 minutos (90 segundos)
- **Calidad**: 1080p mínimo
- **Audio**: Claro y sin ruido de fondo
- **Pantalla**: Mostrar tanto terminal como navegador/Postman
- **Subtítulos**: Opcional, pero recomendado para accesibilidad

## 🚀 Checklist Pre-Demo

- [ ] Aplicación iniciada y funcionando
- [ ] Swagger UI accesible
- [ ] Postman configurado con requests
- [ ] Usuarios de ejemplo creados
- [ ] Datos de prueba disponibles
- [ ] Conexión a internet estable
- [ ] Audio y video funcionando correctamente
