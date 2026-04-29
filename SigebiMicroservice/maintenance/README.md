Maintenance Microservice — Gestión de Mantenimientos
📌 Descripción
Este microservicio es responsable de la gestión integral de mantenimientos de equipos médicos dentro del sistema SIGEBI. Centraliza el registro, seguimiento y programación de mantenimientos preventivos, correctivos y calibraciones:

Registro de mantenimientos correctivos y preventivos
Programación de mantenimientos futuros
Historial de mantenimientos por equipo
Timeline unificado (programados + realizados)
Gestión de tipos de mantenimiento (PREVENTIVO, CORRECTIVO, CALIBRACION)
Validación de técnicos mediante integración con ms-users
Validación de equipos mediante integración con ms-equipment
Consulta de mantenimientos vencidos
Auditoría de creación mediante createdAt
🔐 Seguridad
Este microservicio sí maneja autenticación y autorización mediante tokens JWT. Cada request debe incluir un token válido en el header:

Authorization: Bearer <token>
Permisos requeridos por operación
Permiso	Operaciones
maintenance.create	POST /api/maintenance/schedule
maintenance.read	Todos los GET (/api/maintenance/**)
🏗️ Arquitectura
Este servicio forma parte de una arquitectura de microservicios basada en:

Lenguaje: Java 21
Build: Maven
Framework: Spring Boot 3.x
Base de datos: PostgreSQL
Seguridad: Spring Security + JWT
Registro: Eureka Client
Gateway: Spring Cloud Gateway
Comunicación: Feign Clients
Capas principales
Controller       → Manejo de solicitudes HTTP y autorización
DTO Request      → Validación de datos de entrada (Jakarta Validation)
Service          → Lógica de negocio y validaciones
Repository       → Acceso a datos con Spring Data JPA
Entity           → Modelado de la base de datos
DTO Response     → Respuestas estandarizadas al cliente
Exception        → Manejo global de errores
Security         → Filtro JWT y configuración de seguridad
Client           → Feign clients para integración con otros servicios
Flujo de datos
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
   ├──→ Repository → Entity (PostgreSQL)
   ├──→ EquipmentClient → ms-equipment
   └──→ TechnicianClient → ms-users
       ↓
   DTO Response
       ↓
     Cliente
⚙️ Variables de entorno
Variable	Descripción
DB_HOST	Host de la base de datos
DB_PORT	Puerto de PostgreSQL (por defecto 5432)
DB_NAME	Nombre de la base de datos
DB_USERNAME	Usuario de la base de datos
DB_PASSWORD	Contraseña de la base de datos
JWT_SECRET	Clave secreta compartida para validar tokens JWT
🌐 Puerto
8084
🗄️ Modelo de datos
Entidad principal: Maintenance (mantenimientos realizados)
Maintenance
 ├── MaintenanceType (ManyToOne)
 └── Equipment (referenciado por equipmentId)
 └── Technician (referenciado por technicianId)
Entidad: MaintenanceSchedule (mantenimientos programados)
MaintenanceSchedule
 ├── MaintenanceType (ManyToOne)
 └── Equipment (referenciado por equipmentId)
Entidad: MaintenanceType
MaintenanceType
 └── name: PREVENTIVO, CORRECTIVO, CALIBRACION
Enumeración: MaintenanceStatus
REGISTRADO
FINALIZADO
CANCELADO
PENDIENTE
ATRASADO
Campos de auditoría
Todos los registros incluyen:

Campo	Descripción
createdAt	Fecha y hora de creación (automático)
🔌 Endpoints
📍 Maintenance — /api/maintenance
Método	Endpoint	Descripción	Permiso
POST	/api/maintenance	Registrar mantenimiento	maintenance.create
GET	/api/maintenance	Historial con filtros (paginado)	maintenance.read
GET	/api/maintenance/overdue	Mantenimientos vencidos (paginado)	maintenance.read
GET	/api/maintenance/timeline	Timeline unificado (paginado)	maintenance.read
Parámetros de consulta para GET /api/maintenance:
Parámetro	Tipo	Descripción
equipmentId	Long	ID del equipo (requerido)
type	String	Filtrar por tipo (opcional)
fromDate	LocalDateTime	Fecha inicio (requerido)
toDate	LocalDateTime	Fecha fin (requerido)
page	int	Número de página (default: 0)
size	int	Elementos por página (default: 10)
📍 Maintenance Schedule — /api/maintenance/schedule
Método	Endpoint	Descripción	Permiso
POST	/api/maintenance/schedule	Programar mantenimiento	maintenance.create
📦 Paginación
Todos los endpoints GET de listado soportan paginación mediante query params de Spring Data:

GET /api/maintenance?equipmentId=1&fromDate=2024-01-01T00:00:00&toDate=2024-12-31T23:59:59&page=0&size=10
Parámetro	Descripción	Default
page	Número de página	0
size	Elementos por página	10
sort	Campo y dirección de ordenamiento	—
✅ Validaciones y decisiones técnicas
Validación	Descripción
Existencia de equipo	Se valida que el equipo exista en ms-equipment vía Feign
Existencia de técnico	Se valida que el técnico exista en ms-users vía Feign (userId del token)
Existencia de tipo	Se valida que el tipo de mantenimiento exista en BD local
Fecha no futura	La fecha de mantenimiento no puede ser futura (LocalDateTime)
Fecha futura	La fecha programada debe ser futura (ZonedDateTime, zona America/Bogota)
Descripción mínima	La descripción debe tener al menos 20 caracteres
Sin duplicados	No se permite programar dos mantenimientos activos para misma fecha/equipo
Validación de entrada	Se usa Jakarta Validation con @NotBlank, @NotNull, @Size, @Future
Respuestas estandarizadas	Todas las respuestas usan ApiResponse con estructura uniforme
Manejo global de errores	GlobalExceptionHandler maneja 404, 409, 400, 403, 500
Auditoría	Se registra createdAt automáticamente con @CreationTimestamp
Zona horaria	Fechas programadas usan ZonedDateTime con zona America/Bogota
🔗 Integraciones
Servicio	Descripción
ms-auth	Valida tokens JWT en cada request
ms-equipment	Validación de equipos existentes (puerto 8091, /api/equipments/{id})
ms-users	Validación de técnicos existentes (puerto 8090, /internal/auth/users/{id})
Eureka	Registro y descubrimiento de servicios (puerto 8761)
Gateway	Punto de entrada único en puerto 8080
🚀 Cómo ejecutar
mvn spring-boot:run
O con variables de entorno explícitas:

DB_HOST=localhost DB_PORT=5432 DB_NAME=maintenance \
DB_USERNAME=user DB_PASSWORD=pass \
JWT_SECRET=secret \
mvn spring-boot:run
📁 Estructura del proyecto
maintenance/
├── client/
│   ├── EquipmentClient.java
│   └── TechnicianClient.java
├── config/
│   └── FeignConfig.java
├── controller/
│   └── MaintenanceController.java
├── dto_request/
│   ├── MaintenanceRequest.java
│   └── MaintenanceScheduleRequest.java
├── dto_response/
│   ├── ApiResponse.java
│   ├── MaintenanceResponse.java
│   ├── MaintenanceScheduleResponse.java
│   ├── MaintenanceUnifiedResponse.java
│   ├── EquipmentApiResponse.java
│   └── UserAuthDataResponse.java
├── entities/
│   ├── MaintenanceEntity.java
│   ├── MaintenanceTypeEntity.java
│   ├── MaintenanceScheduleEntity.java
│   └── MaintenanceStatus.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   └── BusinessException.java
├── repository/
│   ├── MaintenanceRepository.java
│   ├── MaintenanceTypeRepository.java
│   └── MaintenanceScheduleRepository.java
├── security/
│   ├── JwtAuthorizationFilter.java
│   ├── JwtUtils.java
│   └── SecurityConfig.java
└── service/
    ├── MaintenanceService.java
    ├── MaintenanceTypeService.java
    └── MaintenanceScheduleService.java
