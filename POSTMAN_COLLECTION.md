# LogiServices Service Facade - Colección Postman Completa

## 🚀 Configuración Inicial

1. **Ejecutar la aplicación:**
   ```bash
   mvn spring-boot:run
   ```

2. **Verificar que la aplicación esté corriendo:**
   - URL: http://localhost:8080
   - Info del servicio: http://localhost:8080/api/v1/envios/info
   - Deberías ver el mensaje de inicio en la consola

## 📋 Colección de Requests - CRUD Completo

### Colección: **LogiServices - Service Facade CRUD**

---

## 📖 OPERACIONES CRUD BÁSICAS

### ✅ 1. LISTAR TODOS LOS ENVÍOS
**Request:** `GET /api/v1/envios`

**URL Completa:** `http://localhost:8080/api/v1/envios`

**Respuesta Esperada (200 OK):**
```json
{
  "total": 9,
  "envios": [
    {
      "id": 123,
      "origen": "Bogotá",
      "destino": "Medellín",
      "estado": "En tránsito",
      "sistemaOrigen": "TMS"
    },
    // ... más envíos
  ]
}
```

### ✅ 2. OBTENER ENVÍO POR ID
**Request:** `GET /api/v1/envios/123`

**URL Completa:** `http://localhost:8080/api/v1/envios/123`

**Respuesta Esperada (200 OK):**
```json
{
  "id": 123,
  "origen": "Bogotá",
  "destino": "Medellín",
  "estado": "En tránsito",
  "sistemaOrigen": "TMS"
}
```

---

## 🆕 OPERACIONES CREATE (POST)

### ✅ 3. CREAR NUEVO ENVÍO
**Request:** `POST /api/v1/envios`

**URL Completa:** `http://localhost:8080/api/v1/envios`

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "origen": "Pereira",
  "destino": "Manizales",
  "estado": "Pendiente",
  "sistemaOrigen": "TMS"
}
```

**Respuesta Esperada (201 Created):**
```json
{
  "id": 792,
  "origen": "Pereira",
  "destino": "Manizales",
  "estado": "Pendiente",
  "sistemaOrigen": "TMS"
}
```

---

### ❌ 4. CREAR ENVÍO CON DATOS INVÁLIDOS
**Request:** `POST /api/v1/envios`

**URL Completa:** `http://localhost:8080/api/v1/envios`

**Body (JSON):**
```json
{
  "origen": "",
  "destino": "Manizales",
  "estado": "Pendiente",
  "sistemaOrigen": "INVALIDO"
}
```

**Respuesta Esperada (400 Bad Request):**
```json
{
  "error": "El origen es obligatorio"
}
```

---

## ✏️ OPERACIONES UPDATE (PUT)

### ✅ 5. ACTUALIZAR ENVÍO EXISTENTE
**Request:** `PUT /api/v1/envios/123`

**URL Completa:** `http://localhost:8080/api/v1/envios/123`

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "origen": "Bogotá",
  "destino": "Medellín",
  "estado": "Entregado",
  "sistemaOrigen": "TMS"
}
```

**Respuesta Esperada (200 OK):**
```json
{
  "id": 123,
  "origen": "Bogotá",
  "destino": "Medellín",
  "estado": "Entregado",
  "sistemaOrigen": "TMS"
}
```

---

### ❌ 6. ACTUALIZAR ENVÍO NO EXISTENTE
**Request:** `PUT /api/v1/envios/999`

**URL Completa:** `http://localhost:8080/api/v1/envios/999`

**Body (JSON):**
```json
{
  "origen": "Bogotá",
  "destino": "Medellín",
  "estado": "Entregado",
  "sistemaOrigen": "TMS"
}
```

**Respuesta Esperada (404 Not Found):**
```json
{
  "error": "No se encontró el envío con ID 999"
}
```

---

## 🗑️ OPERACIONES DELETE

### ✅ 7. ELIMINAR ENVÍO EXISTENTE
**Request:** `DELETE /api/v1/envios/123`

**URL Completa:** `http://localhost:8080/api/v1/envios/123`

**Respuesta Esperada (200 OK):**
```json
{
  "mensaje": "Envío eliminado correctamente",
  "envioEliminado": {
    "id": 123,
    "origen": "Bogotá",
    "destino": "Medellín",
    "estado": "En tránsito",
    "sistemaOrigen": "TMS"
  }
}
```

---

### ❌ 8. ELIMINAR ENVÍO NO EXISTENTE
**Request:** `DELETE /api/v1/envios/999`

**URL Completa:** `http://localhost:8080/api/v1/envios/999`

**Respuesta Esperada (404 Not Found):**
```json
{
  "error": "No se encontró el envío con ID 999"
}
```

---

## 🔍 OPERACIONES DE BÚSQUEDA

### ✅ 9. BUSCAR POR SISTEMA - TMS
**Request:** `GET /api/v1/envios/sistema/TMS`

**URL Completa:** `http://localhost:8080/api/v1/envios/sistema/TMS`

**Respuesta Esperada (200 OK):**
```json
{
  "sistema": "TMS",
  "total": 3,
  "envios": [
    {
      "id": 123,
      "origen": "Bogotá",
      "destino": "Medellín",
      "estado": "En tránsito",
      "sistemaOrigen": "TMS"
    },
    // ... más envíos TMS
  ]
}
```

---

### ✅ 10. BUSCAR POR ESTADO - EN TRÁNSITO
**Request:** `GET /api/v1/envios/estado/En tránsito`

**URL Completa:** `http://localhost:8080/api/v1/envios/estado/En%20tránsito`

**Respuesta Esperada (200 OK):**
```json
{
  "estado": "En tránsito",
  "total": 4,
  "envios": [
    // ... envíos en tránsito
  ]
}
```

---

### ✅ 11. OBTENER ESTADÍSTICAS
**Request:** `GET /api/v1/envios/estadisticas`

**URL Completa:** `http://localhost:8080/api/v1/envios/estadisticas`

**Respuesta Esperada (200 OK):**
```json
{
  "totalEnvios": 9,
  "porSistema": {
    "TMS": 3,
    "ACMS": 3,
    "SMCS": 3
  },
  "porEstado": {
    "En tránsito": 4,
    "Entregado": 3,
    "Pendiente": 2
  }
}
```

---

### ℹ️ 12. INFO DEL SERVICIO
**Request:** `GET /api/v1/envios/info`

**URL Completa:** `http://localhost:8080/api/v1/envios/info`

**Respuesta Esperada (200 OK):**
```json
{
  "servicio": "LogiServices Service Facade",
  "version": "1.0.0",
  "endpoints": {
    "GET /api/v1/envios": "Listar todos los envíos",
    "GET /api/v1/envios/{id}": "Obtener envío por ID",
    "POST /api/v1/envios": "Crear nuevo envío",
    "PUT /api/v1/envios/{id}": "Actualizar envío",
    "DELETE /api/v1/envios/{id}": "Eliminar envío",
    "GET /api/v1/envios/sistema/{sistema}": "Buscar por sistema",
    "GET /api/v1/envios/estado/{estado}": "Buscar por estado",
    "GET /api/v1/envios/estadisticas": "Obtener estadísticas"
  }
}
```

---

## 🧪 FLUJO DE PRUEBA COMPLETO

### Secuencia recomendada para testing:

1. **GET /api/v1/envios** - Ver envíos iniciales
2. **POST /api/v1/envios** - Crear nuevo envío
3. **GET /api/v1/envios/{nuevo-id}** - Verificar creación
4. **PUT /api/v1/envios/{nuevo-id}** - Actualizar envío
5. **GET /api/v1/envios/sistema/TMS** - Buscar por sistema
6. **GET /api/v1/envios/estadisticas** - Ver estadísticas
7. **DELETE /api/v1/envios/{nuevo-id}** - Eliminar envío
8. **GET /api/v1/envios/{nuevo-id}** - Verificar eliminación (404)

## 📝 Notas para el Video (2-3 min)

### Estructura sugerida del video:
1. **0-20s:** Mostrar aplicación corriendo y endpoints disponibles
2. **20-60s:** Demostrar CRUD completo (crear, leer, actualizar, eliminar)
3. **60-90s:** Mostrar búsquedas y estadísticas
4. **90-120s:** Casos de error y validaciones
5. **120-180s:** Arquitectura y próximos pasos

### Puntos clave a mencionar:
- ✅ CRUD completo implementado
- ✅ Validaciones robustas
- ✅ Manejo de errores HTTP apropiado
- ✅ Búsquedas y estadísticas
- ✅ Service Facade pattern
- ✅ Arquitectura en capas
