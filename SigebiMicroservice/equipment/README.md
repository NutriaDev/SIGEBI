# Equipment Microservice — Gestión de Equipos Médicos

## 📌 Descripción

Este microservicio es responsable de la gestión integral de equipos médicos dentro del sistema SIGEBI. Centraliza la administración de toda la información relacionada con los equipos y las entidades que los componen o clasifican:

- Creación, actualización y desactivación de equipos médicos
- Gestión de áreas donde se encuentran los equipos
- Gestión de clasificaciones biomédicas
- Gestión de proveedores asociados
- Gestión de ubicaciones físicas dentro de la institución
- Gestión de estados operativos
- Filtrado de equipos por múltiples criterios
- Activación/desactivación lógica de registros
- Auditoría de creación y actualización

---

## 🔐 Seguridad

Este microservicio **sí maneja autenticación y autorización** mediante tokens JWT. Cada request debe incluir un token válido en el header:

```
Authorization: Bearer <token>
```

### Permisos requeridos por operación

| Permiso | Operaciones |
|---|---|
| `equipment.create` | `POST /api/equipments` y entidades relacionadas |
| `equipment.read` | Todos los `GET` |
| `equipment.update` | `PUT` y `PATCH` |
| `equipment.location.create` | `POST /api/locations` |
| `equipment.location.read` | `GET /api/locations/**` |
| `equipment.location.update` | `PUT`, `PATCH /api/locations/**` |

---

## 🏗️ Arquitectura

Este servicio forma parte de una arquitectura de microservicios basada en:

- **Lenguaje:** Java 21
- **Build:** Maven
- **Framework:** Spring Boot 3.x
- **Base de datos:** PostgreSQL
- **Seguridad:** Spring Security + JWT
- **Registro:** Eureka Client
- **Gateway:** Spring Cloud Gateway

### Capas principales

```
Controller       → Manejo de solicitudes HTTP y autorización
DTO Request      → Validación de datos de entrada (Jakarta Validation)
Service          → Lógica de negocio
Repository       → Acceso a datos con Spring Data JPA
Entity           → Modelado de la base de datos
DTO Response     → Respuestas estandarizadas al cliente
Exception        → Manejo global de errores
Security         → Filtro JWT y configuración de seguridad
```

### Flujo de datos

```
Cliente (con JWT)
       ↓
   Gateway (:8080)
       ↓
JwtAuthorizationFilter
       ↓
   Controller
       ↓
   DTO Request (validación)
       ↓
   Service (lógica de negocio)
       ↓
   Repository
       ↓
   Entity (PostgreSQL)
       ↓
   DTO Response
       ↓
     Cliente
```

---

## ⚙️ Variables de entorno

| Variable | Descripción |
|---|---|
| `DB_HOST` | Host de la base de datos |
| `DB_PORT` | Puerto de PostgreSQL (por defecto `5432`) |
| `DB_NAME` | Nombre de la base de datos |
| `DB_USERNAME` | Usuario de la base de datos |
| `DB_PASSWORD` | Contraseña de la base de datos |
| `JWT_SECRET` | Clave secreta compartida para validar tokens JWT |

---

## 🌐 Puerto

```
8091
```

---

## 🗄️ Modelo de datos

### Entidad principal: Equipment

```
Equipment
 ├── Area              (ManyToOne)
 ├── Classification    (ManyToOne)
 ├── Provider          (ManyToOne, opcional)
 ├── State             (ManyToOne)
 └── Location          (ManyToOne)
```

### Relaciones

| Relación | Cardinalidad |
|---|---|
| Area → Equipment | 1 a muchos |
| Classification → Equipment | 1 a muchos |
| Provider → Equipment | 1 a muchos (opcional) |
| Location → Equipment | 1 a muchos |
| State → Equipment | 1 a muchos |

### Campos de auditoría

Todos los registros incluyen:

| Campo | Descripción |
|---|---|
| `active` | Estado lógico del registro |
| `createdBy` | ID del usuario que creó el registro |
| `updatedBy` | ID del usuario que actualizó el registro |
| `createdAt` | Fecha y hora de creación |
| `updatedAt` | Fecha y hora de última actualización |

---

## 🔌 Endpoints

### 📍 Area — `/api/areas`

| Método | Endpoint | Descripción | Permiso |
|---|---|---|---|
| `POST` | `/api/areas` | Crear área | `equipment.create` |
| `GET` | `/api/areas` | Listar todas (paginado) | `equipment.read` |
| `GET` | `/api/areas/active` | Listar activas (paginado) | `equipment.read` |
| `GET` | `/api/areas/{id}` | Obtener por ID | `equipment.read` |
| `GET` | `/api/areas/name/{name}` | Obtener por nombre | `equipment.read` |
| `PUT` | `/api/areas/{id}` | Actualizar | `equipment.update` |
| `PATCH` | `/api/areas/{id}/deactivate` | Desactivar | `equipment.update` |

---

### 📍 Classification — `/api/classifications`

| Método | Endpoint | Descripción | Permiso |
|---|---|---|---|
| `POST` | `/api/classifications` | Crear clasificación | `equipment.create` |
| `GET` | `/api/classifications` | Listar todas (paginado) | `equipment.read` |
| `GET` | `/api/classifications/active` | Listar activas (paginado) | `equipment.read` |
| `GET` | `/api/classifications/{id}` | Obtener por ID | `equipment.read` |
| `GET` | `/api/classifications/name/{name}` | Obtener por nombre | `equipment.read` |
| `PUT` | `/api/classifications/{id}` | Actualizar | `equipment.update` |
| `PATCH` | `/api/classifications/{id}/deactivate` | Desactivar | `equipment.update` |

---

### 📍 Provider — `/api/providers`

| Método | Endpoint | Descripción | Permiso |
|---|---|---|---|
| `POST` | `/api/providers` | Crear proveedor | `equipment.create` |
| `GET` | `/api/providers` | Listar todos (paginado) | `equipment.read` |
| `GET` | `/api/providers/active` | Listar activos (paginado) | `equipment.read` |
| `GET` | `/api/providers/{id}` | Obtener por ID | `equipment.read` |
| `GET` | `/api/providers/name/{name}` | Obtener por nombre | `equipment.read` |
| `GET` | `/api/providers/email/{email}` | Obtener por email | `equipment.read` |
| `PUT` | `/api/providers/{id}` | Actualizar | `equipment.update` |
| `PATCH` | `/api/providers/{id}/deactivate` | Desactivar | `equipment.update` |

---

### 📍 Location — `/api/locations`

| Método | Endpoint | Descripción | Permiso |
|---|---|---|---|
| `POST` | `/api/locations` | Crear ubicación | `equipment.location.create` |
| `GET` | `/api/locations` | Listar todas (paginado) | `equipment.location.read` |
| `GET` | `/api/locations/active` | Listar activas (paginado) | `equipment.location.read` |
| `GET` | `/api/locations/{id}` | Obtener por ID | `equipment.location.read` |
| `GET` | `/api/locations/type/{type}` | Filtrar por tipo | `equipment.location.read` |
| `GET` | `/api/locations/floor/{floor}` | Filtrar por piso | `equipment.location.read` |
| `GET` | `/api/locations/search?name=` | Buscar por nombre | `equipment.location.read` |
| `PUT` | `/api/locations/{id}` | Actualizar | `equipment.location.update` |
| `PATCH` | `/api/locations/{id}/deactivate` | Desactivar | `equipment.location.update` |

---

### 📍 State — `/api/states`

| Método | Endpoint | Descripción | Permiso |
|---|---|---|---|
| `POST` | `/api/states` | Crear estado | `equipment.create` |
| `GET` | `/api/states` | Listar todos (paginado) | `equipment.read` |
| `GET` | `/api/states/active` | Listar activos (paginado) | `equipment.read` |
| `GET` | `/api/states/{id}` | Obtener por ID | `equipment.read` |
| `PUT` | `/api/states/{id}` | Actualizar | `equipment.update` |
| `PATCH` | `/api/states/{id}/deactivate` | Desactivar | `equipment.update` |

---

### 📍 Equipment — `/api/equipments`

| Método | Endpoint | Descripción | Permiso |
|---|---|---|---|
| `POST` | `/api/equipments` | Crear equipo | `equipment.create` |
| `GET` | `/api/equipments` | Listar todos (paginado) | `equipment.read` |
| `GET` | `/api/equipments/active` | Listar activos (paginado) | `equipment.read` |
| `GET` | `/api/equipments/{id}` | Obtener por ID | `equipment.read` |
| `GET` | `/api/equipments/serie/{serie}` | Obtener por serie | `equipment.read` |
| `GET` | `/api/equipments/search?name=` | Buscar por nombre | `equipment.read` |
| `GET` | `/api/equipments/area?name=` | Filtrar por nombre de área | `equipment.read` |
| `GET` | `/api/equipments/classification?name=` | Filtrar por clasificación | `equipment.read` |
| `GET` | `/api/equipments/provider?name=` | Filtrar por proveedor | `equipment.read` |
| `GET` | `/api/equipments/state?name=` | Filtrar por estado | `equipment.read` |
| `GET` | `/api/equipments/location?name=` | Filtrar por ubicación | `equipment.read` |
| `PUT` | `/api/equipments/{id}` | Actualizar equipo | `equipment.update` |
| `PATCH` | `/api/equipments/{id}/deactivate` | Desactivar equipo | `equipment.update` |
| `PATCH` | `/api/equipments/{id}/activate` | Activar equipo | `equipment.update` |

> **Nota:** Los filtros por área, clasificación, proveedor, estado y ubicación buscan por **nombre** (búsqueda parcial, insensible a mayúsculas), no por ID.

---

## 📦 Paginación

Todos los endpoints `GET` de listado soportan paginación mediante query params de Spring Data:

```
GET /api/equipments?page=0&size=10&sort=active,desc
```

| Parámetro | Descripción | Default |
|---|---|---|
| `page` | Número de página | `0` |
| `size` | Elementos por página | `20` |
| `sort` | Campo y dirección de ordenamiento | — |

---

## ✅ Validaciones y decisiones técnicas

| Validación | Descripción |
|---|---|
| Unicidad de serie | No se permiten equipos con serie duplicada |
| Existencia de relaciones | Se valida que área, clasificación, estado y ubicación existan |
| Unicidad de nombre | No se permiten áreas, clasificaciones, etc. con nombre duplicado |
| Eliminación lógica | Los registros nunca se borran físicamente, se desactivan con `active = false` |
| Validación de entrada | Se usa Jakarta Validation con `@NotBlank`, `@NotNull`, `@Size`, etc. |
| Respuestas estandarizadas | Todas las respuestas usan `ApiResponse` con estructura uniforme |
| Manejo global de errores | `GlobalExceptionHandler` maneja `404`, `409`, `400`, `403`, `500` |
| Auditoría | Se registra `createdBy` y `updatedBy` con el ID del usuario del token JWT |

---

## 🔗 Integraciones

| Servicio | Descripción |
|---|---|
| **ms-auth** | Valida tokens JWT en cada request |
| **ms-cms** | Gestión de imágenes de equipos vía Cloudinary |
| **Eureka** | Registro y descubrimiento de servicios |
| **Gateway** | Punto de entrada único en puerto `8080` |

---

## 🚀 Cómo ejecutar

```bash
mvn spring-boot:run
```

O con variables de entorno explícitas:

```bash
DB_HOST=localhost DB_PORT=5432 DB_NAME=equipment \
DB_USERNAME=user DB_PASSWORD=pass \
JWT_SECRET=secret \
mvn spring-boot:run
```

---

## 📁 Estructura del proyecto

```
equipment/
├── constants/
├── controller/
├── dto_request/
├── dto_response/
├── entities/
├── exception/
├── repository/
├── security/
│   ├── JwtAuthorizationFilter.java
│   ├── JwtUtils.java
│   └── SecurityConfig.java
├── service/
└── util/
```