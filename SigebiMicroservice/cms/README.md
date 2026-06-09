# CMS Microservice — Gestión de Imágenes de Equipos Médicos

## 📌 Descripción

Este microservicio es responsable de la gestión de imágenes de equipos médicos dentro del sistema **SIGEBI**. Actúa como un servicio de medios centralizado utilizando **Cloudinary** como proveedor de almacenamiento en la nube:

- Subida de imágenes para equipos médicos
- Consulta de imagen por equipo
- Eliminación de imagen (Cloudinary + BD)
- Reemplazo automático de imagen (elimina la anterior antes de subir la nueva)
- Validación de existencia del equipo vía Feign Client con ms-equipment

---

## 🏗️ Arquitectura

| Componente    | Tecnología                         |
|---------------|------------------------------------|
| Lenguaje      | Java 21                            |
| Build         | Maven                              |
| Framework     | Spring Boot 3.x                    |
| Base de datos | PostgreSQL                         |
| Seguridad     | Spring Security + JWT              |
| Almacenamiento| Cloudinary (imágenes en la nube)   |
| Registro      | Eureka Client                      |
| Gateway       | Spring Cloud Gateway               |
| Comunicación  | Feign Client (OpenFeign)           |

### Capas principales

```
Controller      → Manejo de solicitudes HTTP y autorización
Service         → Lógica de negocio (subida, consulta, eliminación)
Repository      → Acceso a datos con Spring Data JPA
Entity          → Modelado de la base de datos
DTO Response    → Respuestas estandarizadas al cliente
Exception       → Manejo global de errores
Security        → Filtro JWT y configuración de seguridad
Client          → Feign client para integración con ms-equipment
Config          → Configuración de Cloudinary
```

### Flujo de datos

```
Cliente (con JWT)
      ↓
Gateway (:8080)
      ↓
JwtAuthorizationFilter
      ↓
Controller (/api/media)
      ↓
Service (MediaService)
      ↓
      ├──→ EquipmentClient → ms-equipment (valida equipo)
      ├──→ Cloudinary (subir/eliminar imagen)
      └──→ Repository → PostgreSQL (metadata)
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

| Permiso       | Operaciones                          |
|---------------|--------------------------------------|
| `cms.create`  | `POST /api/media/equipment/{id}`     |
| `cms.read`    | `GET /api/media/equipment/{id}`      |
| `cms.delete`  | `DELETE /api/media/equipment/{id}`   |

> **Nota:** El controlador actualmente no tiene anotaciones `@PreAuthorize` específicas, pero el SecurityConfig exige autenticación para todas las rutas mediante `.anyRequest().authenticated()`.

---

## 🌐 Puerto

```
8082
```

---

## ⚙️ Variables de entorno

| Variable                  | Descripción                                    |
|---------------------------|------------------------------------------------|
| `DB_HOST`                 | Host de la base de datos                       |
| `DB_PORT`                 | Puerto de PostgreSQL (por defecto `5432`)      |
| `DB_NAME`                 | Nombre de la base de datos                     |
| `DB_USERNAME`             | Usuario de la base de datos                    |
| `DB_PASSWORD`             | Contraseña de la base de datos                 |
| `JWT_SECRET`              | Clave secreta compartida para validar tokens   |
| `CLOUDINARY_CLOUD_NAME`   | Cloud name de Cloudinary                       |
| `CLOUDINARY_API_KEY`      | API Key de Cloudinary                          |
| `CLOUDINARY_API_SECRET`   | API Secret de Cloudinary                       |

---

## 🗄️ Modelo de datos

### Entidad: `EquipmentImage`

```
EquipmentImage
├── id            (Long, PK, auto-generado)
├── equipmentId   (Long, unique, not null) → referencia al equipo en ms-equipment
├── imageUrl      (String, not null)       → URL pública de Cloudinary
├── publicId      (String, not null)       → ID en Cloudinary para gestionar la imagen
└── uploadedAt    (LocalDateTime)          → Fecha de subida
```

### Tabla: `equipment_images`

| Columna       | Tipo           | Restricciones             |
|---------------|----------------|---------------------------|
| id            | BIGSERIAL      | PRIMARY KEY               |
| equipment_id  | BIGINT         | NOT NULL, UNIQUE          |
| image_url     | VARCHAR(255)   | NOT NULL                  |
| public_id     | VARCHAR(255)   | NOT NULL                  |
| uploaded_at   | TIMESTAMP      | nullable                  |

### Relaciones

| Relación                        | Cardinalidad  |
|---------------------------------|---------------|
| Equipment (ms-equipment) → Image| 1 a 1         |

> Solo existe una imagen por equipo. Si se sube una nueva, la anterior se elimina automáticamente de Cloudinary y de la BD.

---

## 🔌 Endpoints

### 📍 Media — `/api/media`

| Método  | Endpoint                              | Descripción                                      | Permiso    |
|---------|---------------------------------------|--------------------------------------------------|------------|
| `POST`  | `/api/media/equipment/{equipmentId}`  | Subir imagen para un equipo                      | `cms.create` |
| `GET`   | `/api/media/equipment/{equipmentId}`  | Obtener imagen de un equipo                      | `cms.read`   |
| `DELETE`| `/api/media/equipment/{equipmentId}`  | Eliminar imagen de un equipo                     | `cms.delete` |

---

## 📦 Paginación

Este microservicio no expone endpoints de listado, por lo que no aplica paginación.

---

## ✅ Validaciones y decisiones técnicas

| Validación / Decisión              | Descripción                                                                               |
|------------------------------------|-------------------------------------------------------------------------------------------|
| Unicidad de imagen por equipo      | Solo una imagen por equipo (equipmentId es UNIQUE). Subir otra reemplaza la anterior      |
| Existencia del equipo              | Se valida que el equipo exista en `ms-equipment` vía Feign Client antes de subir          |
| Eliminación en cascada             | Al subir una nueva imagen, se elimina la anterior de Cloudinary y de la BD                |
| Límite de tamaño                   | Archivos de hasta **5 MB** (configurado en `application.yml`)                             |
| Almacenamiento externo             | Las imágenes se almacenan en Cloudinary, no en la BD. La BD solo guarda metadatos         |
| Organización en carpetas           | Las imágenes se suben a la carpeta `sigebi/equipments` en Cloudinary                      |
| Public ID predecible               | El public_id sigue el patrón `equipment_{equipmentId}` para facilitar la gestión          |
| Respuestas estandarizadas          | Se usa `ImageResponse` con estructura uniforme                                             |
| Manejo global de errores           | `GlobalExceptionHandler` maneja `404` y `500`                                              |
| Token forwarding                   | `FeignConfig` propaga el token JWT del request original al llamar a `ms-equipment`        |

---

## 🔗 Integraciones

| Servicio          | Descripción                                                               |
|-------------------|---------------------------------------------------------------------------|
| `ms-auth`         | Valida tokens JWT en cada request                                         |
| `ms-equipment`    | Validación de equipos existentes (puerto `8091`, `GET /api/equipments/{id}`) |
| `Cloudinary`      | Almacenamiento y gestión de imágenes en la nube                           |
| `Eureka`          | Registro y descubrimiento de servicios (puerto `8761`)                    |
| `Gateway`         | Punto de entrada único en puerto `8080`                                   |

---

## 🚀 Cómo ejecutar

```bash
mvn spring-boot:run
```

Con variables de entorno explícitas:

```bash
DB_HOST=localhost DB_PORT=5432 DB_NAME=cms DB_USERNAME=user DB_PASSWORD=pass JWT_SECRET=secret CLOUDINARY_CLOUD_NAME=mycloud CLOUDINARY_API_KEY=key CLOUDINARY_API_SECRET=secret mvn spring-boot:run
```

---

## 📁 Estructura del proyecto

```
cms/
├── src/main/java/sigebi/cms/
│   ├── clients/
│   │   └── EquipmentClient.java          # Feign client para ms-equipment
│   ├── config/
│   │   └── CloudinaryConfig.java         # Configuración de Cloudinary
│   ├── controller/
│   │   └── MediaController.java          # REST controller
│   ├── DTO/
│   │   └── ImageResponse.java            # DTO de respuesta
│   ├── entities/
│   │   └── EquipmentImageEntity.java     # Entidad JPA
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java   # Manejo global de errores
│   │   └── ResourceNotFoundException.java
│   ├── repository/
│   │   └── EquipmentImageRepository.java # Spring Data JPA repository
│   ├── security/
│   │   ├── FeignConfig.java              # Propagación de JWT en Feign
│   │   ├── JwtAuthorizationFilter.java   # Filtro de autorización JWT
│   │   ├── JwtUtils.java                 # Utilidades JWT
│   │   └── SecurityConfig.java           # Configuración de Spring Security
│   ├── service/
│   │   └── MediaService.java             # Lógica de negocio
│   └── ApplicationCMS.java               # Punto de entrada
├── src/test/java/sigebi/cms/
│   ├── ApplicationTests.java             # Test de contexto (deshabilitado)
│   └── service/
│       └── MediaServiceTest.java         # Pruebas unitarias del servicio
├── src/main/resources/
│   └── application.yml                   # Configuración de la aplicación
├── pom.xml
└── .env
```
