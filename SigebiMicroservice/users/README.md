# 👥 ms-users — Gestión de Usuarios

Microservicio responsable de la gestión de usuarios, roles y entidades dentro del sistema **SIGEBI**.

---

## 📌 Descripción

Centraliza la administración de identidades del sistema, incluyendo:

- Creación, actualización y desactivación de usuarios
- Asignación de roles definidos por el sistema
- Validación de datos críticos antes de su persistencia
- Control del ciclo de vida del usuario (activar/desactivar/eliminar)
- Gestión de roles y compañías
- Validación de credenciales para `ms-auth` mediante endpoints internos

**⚠️ Este microservicio NO maneja autenticación ni emisión de tokens.** La seguridad, sesiones y JWT son responsabilidad de `ms-auth`.

---

## 🏗️ Arquitectura

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 17 |
| Build | Maven |
| Framework | Spring Boot 3.x |
| Base de datos | PostgreSQL |
| Seguridad | Spring Security + JWT |
| Registro | Eureka Client |
| Gateway | Spring Cloud Gateway |

### Capas principales

```
Controller       → Manejo de solicitudes HTTP y autorización
DTO Request      → Validación de datos de entrada (Jakarta Validation)
Service          → Lógica de negocio y validaciones
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

## 🔐 Seguridad

Cada request debe incluir un token JWT válido:

```
Authorization: Bearer <token>
```

### Permisos requeridos por operación

| Permiso | Operaciones |
|---|---|
| `users.create.admin` / `.supervisor` / `.tecnico` | `POST /api/users/users-create` |
| `users.read.admin` / `.supervisor` / `.tecnico` | Todos los `GET /api/users/**` |
| `users.update.admin` / `.supervisor` / `.tecnico` | `PATCH /api/users/edit-user/{id}`, `/deactive-user/{id}`, `/activate-user/{id}` |
| `users.delete.admin` / `.supervisor` / `.tecnico` | `DELETE /api/users/deletehard-user/{id}` |
| `roles.delete` | `DELETE /api/users/deletehard-role/{id}` |

> Los endpoints `/internal/auth/**` no requieren autenticación (uso interno entre microservicios).

---

## 🌐 Puerto

```
8090
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

## 🗄️ Modelo de datos

### Entidad principal: `UserEntity` — Tabla `users`

```
UserEntity
├── idUsers        (Long, PK)
├── name           (String, not null)
├── lastname       (String, not null)
├── birthDate      (Date, not null)
├── phone          (String, not null)
├── id             (Long, not null, unique) — documento de identidad
├── email          (String, not null, unique)
├── passwordHash   (String, not null) — BCrypt
├── active         (Boolean, default true)
├── createdAt      (Date, @CreationTimestamp)
├── updatedAt      (Date, @UpdateTimestamp)
├── companyId      (CompanyEntity, @ManyToOne, FK → company)
└── role           (RoleEntity, @ManyToOne, FK → role)
```

### Entidad: `RoleEntity` — Tabla `role`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `Long` (PK) | Identificador único |
| `nameRole` | `String` (unique) | SUPERADMIN, ADMIN, SUPERVISOR, TECNICO |
| `status` | `Boolean` | Activo/inactivo |

### Entidad: `CompanyEntity` — Tabla `company`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `Long` (PK) | Identificador único |
| `nameCompany` | `String` (unique) | SIGEBI, DRAGONESDEV, SANITAS, etc. |
| `status` | `Boolean` | Activo/inactivo |

### Relaciones

| Relación | Cardinalidad |
|---|---|
| Company → User | 1 a muchos |
| Role → User | 1 a muchos |

### Campos de auditoría

| Campo | Descripción |
|---|---|
| `createdAt` | Fecha y hora de creación (automático) |
| `updatedAt` | Fecha y hora de última actualización (automático) |

---

## 🔌 Endpoints

### 📍 Users — `/api/users`

| Método | Endpoint | Descripción | Permiso |
|---|---|---|---|
| `POST` | `/api/users/users-create` | Crear nuevo usuario | `users.create.*` |
| `GET` | `/api/users/get-all-users` | Listar todos los usuarios | `users.read.*` |
| `GET` | `/api/users/get-user-by-id/{id}` | Obtener usuario por ID | `users.read.*` |
| `GET` | `/api/users/get-user-by-email/{email}` | Obtener usuario por email | `users.read.*` |
| `GET` | `/api/users/get-all-by-active/{active}` | Listar roles por estado | `users.read.*` |
| `PATCH` | `/api/users/edit-user/{id}` | Editar información del usuario (campos individuales) | `users.update.*` |
| `PATCH` | `/api/users/deactive-user/{id}` | Desactivar usuario (soft delete) | `users.update.*` |
| `PATCH` | `/api/users/activate-user/{id}` | Activar usuario desactivado | `users.update.*` |
| `DELETE` | `/api/users/deletehard-user/{id}` | Eliminar usuario definitivamente (hard delete) | `users.delete.*` |

### 📍 Roles — `/api/users`

| Método | Endpoint | Descripción | Permiso |
|---|---|---|---|
| `GET` | `/api/users/get-all-roles` | Listar todos los roles | público |
| `POST` | `/api/users/save-rol` | Crear o actualizar un rol | público |
| `DELETE` | `/api/users/deletehard-role/{id}` | Eliminar un rol definitivamente | `roles.delete` |

### 📍 Internal Auth — `/internal/auth`

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/internal/auth/validate` | Validar credenciales (email + password) |
| `GET` | `/internal/auth/users/{id}` | Obtener datos de autenticación por ID |
| `GET` | `/internal/auth/users/by-email/{email}` | Obtener info básica por email |
| `POST` | `/internal/auth/users/{id}/password` | Actualizar contraseña hasheada directamente |

> Los endpoints `/internal/auth/**` están diseñados para comunicación entre microservicios (ms-auth → ms-users) y no requieren autenticación.

---

## ✅ Validaciones y decisiones técnicas

| Validación/Decisión | Descripción |
|---|---|
| Unicidad de email | No se permiten emails duplicados |
| Unicidad de teléfono | No se permiten números telefónicos duplicados |
| Unicidad de documento | No se permiten documentos de identidad duplicados |
| Validación de email | Formato válido + dominio no temporal (yopmail, guerrillamail, etc.) |
| Normalización de email | Trim + lowercase automático |
| Edad mínima | El usuario debe ser mayor de 18 años |
| Seguridad de contraseña | Mínimo 8 caracteres, 1 mayúscula, 1 dígito, 1 carácter especial |
| Encriptación | Contraseñas almacenadas con BCrypt |
| Inmutabilidad de rol/compañía | No se puede cambiar el rol o compañía después de la creación |
| Protección SUPERADMIN | No puede ser desactivado ni eliminado |
| Soft delete | El usuario debe estar desactivado antes de eliminarlo físicamente |
| Eliminación lógica | Los registros se desactivan con `active = false` |
| Respuestas estandarizadas | Todas las respuestas usan `Response` con estructura uniforme |
| Manejo global de errores | `GlobalExceptionHandler` maneja `400`, `403`, `404`, `409`, `500` |
| Auditoría | Se registra `createdAt` y `updatedAt` automáticamente |

---

## 🔗 Integraciones

| Servicio | Descripción |
|---|---|
| **ms-auth** | Consume endpoints internos (`/internal/auth/**`) para validación de credenciales y obtención de datos de usuario |
| **ms-maintenance** | Consulta datos de técnicos vía Feign |
| **ms-reportsandaudit** | Consulta datos de usuario para reportes |
| **Eureka** | Registro y descubrimiento de servicios (puerto `8761`) |
| **Gateway** | Punto de entrada único en puerto `8080` |

---

## 🚀 Cómo ejecutar

```bash
mvn spring-boot:run
```

Con variables de entorno explícitas:

```bash
DB_HOST=localhost DB_PORT=5432 DB_NAME=users DB_USERNAME=user DB_PASSWORD=pass JWT_SECRET=secret mvn spring-boot:run
```

---

## 📁 Estructura del proyecto

```
users/
├── constants/
│   └── ErrorTitles.java
├── controller/
│   ├── UserController.java
│   └── InternalAuthController.java
├── dto_request/
│   ├── CreateUsersRequest.java
│   ├── InternalAuthValidateRequest.java
│   ├── RoleRequest.java
│   ├── UpdatePasswordInternalDto.java
│   └── UpdateUserRequest.java
├── dto_response/
│   ├── CompanyResponse.java
│   ├── Response.java
│   ├── RoleResponse.java
│   ├── UserAuthDataResponse.java
│   ├── UserBasicResponse.java
│   └── UserResponse.java
├── entities/
│   ├── CompanyEntity.java
│   ├── RoleEntity.java
│   └── UserEntity.java
├── exception/
│   ├── BusinessException.java
│   ├── CompanyNotFoundException.java
│   ├── EmailException.java
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── RoleNotFoundException.java
│   └── UserNotFoundException.java
├── repository/
│   ├── CompanyRepository.java
│   ├── RoleRepository.java
│   └── UsersRepository.java
├── security/
│   ├── JwtAuthorizationFilter.java
│   ├── JwtUtils.java
│   └── SecurityConfig.java
├── service/
│   ├── impl/
│   │   └── EncryptServiceImpl.java
│   ├── CompanyService.java
│   ├── EncryptService.java
│   ├── InternalAuthService.java
│   ├── RoleService.java
│   └── UsersService.java
└── util/
    └── ApiResponse.java
```
