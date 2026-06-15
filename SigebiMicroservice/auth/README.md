# 🔐 ms-auth — Autenticación y Seguridad

Microservicio central de **autenticación, autorización y gestión de sesiones** del sistema SIGEBI. Actúa como **Token Issuer** y gestor de sesiones, delegando la validación de identidad a **ms-users** mediante **OpenFeign**.

---

## 📌 Descripción

**ms-auth** no es el dueño de la identidad ni de los roles como fuente de verdad. La identidad, estado y roles del usuario pertenecen a `ms-users`. Auth resuelve permisos derivados de esos roles y los embebe dentro del JWT para habilitar autorización distribuida.

**Responsabilidades clave:**
- Delegar validación de credenciales a `ms-users`
- Crear y gestionar sesiones
- Resolver permisos granulares a partir de roles
- Generar Access Tokens (JWT firmados con HMAC)
- Generar y rotar Refresh Tokens persistidos
- Implementar token rotation obligatoria
- Permitir revocación controlada de sesiones
- Envío de notificaciones por correo electrónico (login, reset password)

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
| Comunicación | OpenFeign (con ms-users) |
| Correo | JavaMailSender (SMTP) |

### Capas principales

```
Controller       → Manejo de solicitudes HTTP
DTO Request      → Validación de datos de entrada (Jakarta Validation)
Service          → Lógica de negocio (login, refresh, logout, tokens)
Repository       → Acceso a datos con Spring Data JPA
Entity           → Modelado de la base de datos
DTO Response     → Respuestas estandarizadas al cliente
Exception        → Manejo global de errores
Security         → Filtro JWT y configuración de seguridad
Client           → Feign client para integración con ms-users
```

### Flujo de autenticación

```
Cliente
   ↓ POST /auth/login
AuthController
   ↓
LoginService
   ├──→ UserInternalClient.validate() → ms-users
   ├──→ Crea SessionEntity
   ├──→ PermissionService.getPermissionsByRoles()
   ├──→ JwtService.generate() → Access Token
   ├──→ Crea RefreshTokenEntity
   └──→ EmailService.sendLoginNotification() (async)
   ↓
LoginResponse (accessToken + refreshToken + sessionId)
```

---

## 🔐 Seguridad

Cada request debe incluir un token JWT válido:

```
Authorization: Bearer <token>
```

### Endpoints públicos (no requieren autenticación)

| Endpoint | Descripción |
|---|---|
| `POST /auth/login` | Inicio de sesión |
| `POST /auth/refresh` | Rotación de tokens |
| `POST /auth/logout` | Cierre de sesión |
| `POST /auth/forgot-password` | Solicitar restablecimiento de contraseña |
| `POST /auth/reset-password` | Confirmar restablecimiento de contraseña |

### Permisos (requieren autenticación)

Los endpoints de sesión requieren que el usuario posea un token JWT válido (autenticado):

| Endpoint | Descripción |
|---|---|
| `GET /auth/sessions` | Historial de sesiones del usuario autenticado |
| `GET /auth/sessions/active` | Sesiones activas del usuario autenticado |

### Access Token (JWT)

Contiene los siguientes claims:

| Claim | Descripción |
|---|---|
| `sub` | `userId` |
| `sessionId` | UUID de la sesión |
| `email` | Correo del usuario |
| `name` | Nombre del usuario |
| `roles` | Lista de roles asignados |
| `permissions` | Lista de permisos granulares |
| `iat` | Fecha de emisión |
| `exp` | Fecha de expiración (15 min por defecto) |

Características:
- Firmado con HMAC-SHA
- Stateless (no requiere consulta a BD para validación)
- Permite autorización distribuida en microservicios

### Refresh Token

- Persistido en base de datos
- Asociado directamente a una sesión
- Expiración configurable
- Estado activo/inactivo
- Rotación obligatoria en cada uso

---

## 🌐 Puerto

```
9090
```

---

## ⚙️ Variables de entorno

| Variable | Descripción |
|---|---|
| `DB_HOST` | Host de la base de datos |
| `DB_PORT` | Puerto de PostgreSQL |
| `DB_NAME` | Nombre de la base de datos |
| `DB_USERNAME` | Usuario de la base de datos |
| `DB_PASSWORD` | Contraseña de la base de datos |
| `JWT_SECRET` | Clave secreta para firmar/validar JWT |
| `JWT_EXPIRATION_MINUTES` | TTL del access token (default: 15) |
| `MAIL_USERNAME` | Usuario SMTP |
| `MAIL_PASSWORD` | Contraseña SMTP |
| `EUREKA_URL` | URL del servidor Eureka (producción) |

---

## 🗄️ Modelo de datos

### Entidad: `SessionEntity`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `UUID` (PK) | Identificador único de sesión |
| `userId` | `Long` | ID del usuario |
| `loginAt` | `Instant` | Fecha de inicio de sesión |
| `lastActivityAt` | `Instant` | Última actividad |
| `logoutAt` | `Instant` | Fecha de cierre (nullable) |
| `active` | `Boolean` | Estado de la sesión |

### Entidad: `RefreshTokenEntity`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `UUID` (PK) | Identificador único |
| `token` | `String` (unique) | Token de 64 bytes (Base64 URL-safe) |
| `userId` | `Long` | ID del usuario |
| `sessionId` | `UUID` | Sesión asociada |
| `expiresAt` | `Instant` | Fecha de expiración |
| `active` | `Boolean` | Estado del token |

### Entidad: `PasswordResetTokenEntity`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `UUID` (PK) | Identificador único |
| `token` | `UUID` (unique) | Token enviado por email |
| `userId` | `Long` | ID del usuario |
| `userEmail` | `String` | Correo del usuario |
| `expiresAt` | `Instant` | Fecha de expiración |
| `used` | `boolean` | Si ya fue usado |

### Entidad: `RevokedTokenEntity`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `UUID` (PK) | Identificador único |
| `token` | `String` (unique) | Token revocado |
| `revokedAt` | `Instant` | Fecha de revocación |
| `tokenType` | `String` | ACCESS o REFRESH |

### Entidad: `AuthRoleEntity`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `UUID` (PK) | Identificador único |
| `name` | `String` (unique) | SUPERADMIN, ADMIN, SUPERVISOR, TECNICO |

### Entidad: `AuthPermissionEntity`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `UUID` (PK) | Identificador único |
| `name` | `String` (unique) | `users.create.admin`, `equipment.read`, etc. |
| `description` | `String` | Descripción del permiso |

### Relaciones

| Relación | Cardinalidad |
|---|---|
| Session → RefreshToken | 1 a muchos |
| AuthRole → RolePermission | 1 a muchos |
| AuthPermission → RolePermission | 1 a muchos |
| UserRole (tabla) → AuthRole | muchos a muchos |

---

## 🔌 Endpoints

### 📍 Auth — `/auth`

| Método | Endpoint | Descripción | Autenticación |
|---|---|---|---|
| `POST` | `/auth/login` | Iniciar sesión (genera access + refresh token) | No |
| `POST` | `/auth/refresh` | Rotar tokens (nuevo access + refresh) | No |
| `POST` | `/auth/logout` | Cerrar sesión (invalida refresh tokens) | No |
| `POST` | `/auth/forgot-password` | Solicitar restablecimiento de contraseña | No |
| `POST` | `/auth/reset-password` | Confirmar restablecimiento de contraseña | No |
| `GET` | `/auth/secure-test` | Endpoint de prueba (devuelve datos del token) | Sí |
| `GET` | `/auth/sessions` | Historial de sesiones del usuario (paginado) | Sí |
| `GET` | `/auth/sessions/active` | Sesiones activas del usuario (paginado) | Sí |

### Flujo de Login

```
POST /auth/login
Request:
{
  "email": "usuario@correo.com",
  "password": "MiPassword123!"
}

Response:
{
  "accessToken": "eyJhbGci...",
  "refreshToken": "aBcDeF...",
  "expiresAt": "2025-01-01T00:00:00Z",
  "sessionId": "uuid-de-sesion"
}
```

### Flujo de Refresh

```
POST /auth/refresh
Request:
{
  "refreshToken": "aBcDeF..."
}

Response:
{
  "accessToken": "eyJhbGci...",
  "refreshToken": "nuevo-token-rotado",
  "expiresAt": "2025-01-01T00:15:00Z"
}
```

### Flujo de Logout

```
POST /auth/logout?sessionId=uuid-de-sesion

Response:
{
  "status": "200",
  "message": "Logout successful. All tokens revoked."
}
```

### Flujo de Reset Password

```
1. POST /auth/forgot-password
   { "email": "usuario@correo.com" }
   → Envío de email con token

2. POST /auth/reset-password
   {
     "token": "uuid-del-token",
     "newPassword": "NuevaPass123!",
     "confirmPassword": "NuevaPass123!"
   }
   → Contraseña actualizada + sesiones revocadas
```

---

## ✅ Validaciones y decisiones técnicas

| Validación/Decisión | Descripción |
|---|---|
| Separación Auth / Users | ms-Users es fuente de verdad de identidad; ms-Auth gestiona seguridad, sesiones y tokens |
| Access Token Stateless | Validación local sin depender de otros servicios |
| Token Rotation | Cada refresh invalida el token anterior y genera uno nuevo |
| Persistencia de Sesiones | Permite auditoría, revocación manual y control multi-dispositivo |
| Contraseña segura | Mínimo 8 caracteres, mayúscula, minúscula, dígito, carácter especial |
| Reset invalida sesiones | Al cambiar contraseña se revocan todas las sesiones activas |
| Notificaciones asíncronas | Emails de login y reset se envían de forma asíncrona |
| Seguridad en reset | Se oculta si el email existe o no (previene enumeración) |
| Respuestas estandarizadas | Todas las respuestas usan `Response` con estructura uniforme |
| Manejo global de errores | `GlobalExceptionHandler` maneja `401`, `403`, `400`, `500` |

---

## 🔗 Integraciones

| Servicio | Tipo | Descripción |
|---|---|---|
| **ms-users** | Feign | Validación de credenciales (`POST /internal/auth/validate`), obtención de datos de usuario |
| **ms-equipment** | JWT | Validación de tokens en requests (autorización distribuida) |
| **ms-inventory** | JWT | Validación de tokens en requests (autorización distribuida) |
| **ms-maintenance** | JWT | Validación de tokens en requests (autorización distribuida) |
| **ms-cms** | JWT | Validación de tokens en requests (autorización distribuida) |
| **ms-reportsandaudit** | JWT | Validación de tokens en requests (autorización distribuida) |
| **Eureka** | Registro | Registro y descubrimiento de servicios (puerto `8761`) |
| **Gateway** | Proxy | Punto de entrada único en puerto `8080` |

---

## 🚀 Cómo ejecutar

```bash
mvn spring-boot:run
```

Con variables de entorno explícitas:

```bash
DB_HOST=localhost DB_PORT=5432 DB_NAME=auth DB_USERNAME=user DB_PASSWORD=pass JWT_SECRET=secret MAIL_USERNAME=correo@gmail.com MAIL_PASSWORD=password mvn spring-boot:run
```

---

## 📁 Estructura del proyecto

```
auth/
├── client/
│   └── UserInternalClient.java
├── constants/
│   └── ErrorTitles.java
├── controller/
│   └── AuthController.java
├── DTO/
│   ├── request/
│   │   ├── ForgotPasswordRequest.java
│   │   ├── InternalAuthValidateRequest.java
│   │   ├── LoginRequest.java
│   │   ├── LogoutRequest.java
│   │   ├── RefreshRequest.java
│   │   ├── ResetPasswordRequest.java
│   │   └── UpdatePasswordFeignRequest.java
│   └── response/
│       ├── LoginResponse.java
│       ├── LogoutResponse.java
│       ├── RefreshResponse.java
│       ├── Response.java
│       ├── SessionResponse.java
│       ├── UserAuthDataResponse.java
│       └── UserBasicResponse.java
├── entities/
│   ├── AuthPermissionEntity.java
│   ├── AuthRoleEntity.java
│   ├── PasswordResetTokenEntity.java
│   ├── RefreshTokenEntity.java
│   ├── RevokedTokenEntity.java
│   ├── RolePermissionEntity.java
│   ├── SessionEntity.java
│   └── UserRoleEntity.java
├── exceptions/
│   ├── EmailSendException.java
│   ├── ExpiredRefreshTokenException.java
│   ├── GlobalExceptionHandler.java
│   ├── InvalidRefreshTokenException.java
│   ├── RevokedRefreshTokenException.java
│   ├── SessionNotActiveException.java
│   └── SessionNotFoundException.java
├── repository/
│   ├── AuthPermissionRepository.java
│   ├── AuthRoleRepository.java
│   ├── PasswordResetTokenRepository.java
│   ├── RefreshTokenRepository.java
│   ├── RevokedRepository.java
│   ├── RevokedTokenRepository.java
│   ├── RolePermissionRepository.java
│   └── SessionRepository.java
├── security/
│   ├── JwtAuthorizationFilter.java
│   └── SecurityConfig.java
├── service/
│   ├── impl/
│   │   ├── JwtServiceImpl.java
│   │   ├── LoginServiceImpl.java
│   │   ├── LogoutServiceImpl.java
│   │   ├── RefreshTokenServiceImpl.java
│   │   ├── SessionServiceImpl.java
│   │   └── UserPermissionServiceImpl.java
│   ├── EmailService.java
│   ├── JwtService.java
│   ├── LoginService.java
│   ├── LogoutServive.java
│   ├── PasswordResetService.java
│   ├── PermissionService.java
│   ├── RefreshTokenService.java
│   ├── SessionService.java
│   └── UserPermissionService.java
└── utils/
    └── ApiResponse.java
```
