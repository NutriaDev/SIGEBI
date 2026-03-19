# Equipment Microservice (Gestión de Equipos)

## 📌 Descripción

Este microservicio es responsable de la gestión de equipos dentro del sistema. Su objetivo es centralizar la administración de la información relacionada con los equipos, así como las entidades que los componen o clasifican, incluyendo:

* Creación, actualización y desactivación de equipos
* Gestión de áreas donde se encuentran los equipos
* Gestión de clasificaciones de equipos
* Gestión de proveedores asociados a los equipos
* Gestión de ubicaciones físicas dentro de la institución
* Gestión de estados operativos de los equipos
* Control del ciclo de vida de los equipos registrados
* Filtrado de equipos por diferentes criterios
* Auditoría de creación y actualización
* Activación/desactivación lógica de registros

> ⚠️ Este microservicio **no maneja autenticación ni autorización**.

---

## 🏗️ Arquitectura

Este servicio forma parte de una arquitectura basada en:

* Java
* Maven
* Spring Boot (Microservices)

Se implementa una arquitectura en capas para mejorar la mantenibilidad, escalabilidad y reutilización del código.

### Capas principales

* **Controller** → Manejo de solicitudes HTTP
* **DTO Request** → Validación de datos de entrada
* **Service** → Lógica de negocio
* **Util / Constants** → Clases auxiliares
* **Repository** → Acceso a datos con Spring Data JPA
* **Entity** → Modelado de la base de datos
* **DTO Response** → Respuestas al cliente

---

## 🔄 Flujo de datos

```text
Cliente
↓
Controller
↓
DTO Request
↓
Service (reglas de negocio)
↓
Util / Validaciones
↓
Repository
↓
Entity
↓
DTO Response
↓
Cliente
```

---

## 🗄️ Modelo de datos

Entidad principal: **Equipment**

```text
Equipment
 ├── Area
 ├── Classification
 ├── Provider
 ├── State
 └── Location
```

### Relaciones

* Area 1 → * Equipment
* Classification 1 → * Equipment
* Provider 1 → * Equipment
* Location 1 → * Equipment
* State 1 → * Equipment

---

## 🔌 Endpoints

### 📍 Area

* `POST /api/areas`
* `GET /api/areas`
* `GET /api/areas/active`
* `GET /api/areas/{id}`
* `GET /api/areas/name/{name}`
* `PUT /api/areas/{id}`
* `PATCH /api/areas/{id}/deactivate`

---

### 📍 Classification

* `POST /api/classifications`
* `GET /api/classifications`
* `GET /api/classifications/active`
* `GET /api/classifications/{id}`
* `GET /api/classifications/name/{name}`
* `PUT /api/classifications/{id}`
* `PATCH /api/classifications/{id}/deactivate`

---

### 📍 Provider

* `POST /api/providers`
* `GET /api/providers`
* `GET /api/providers/active`
* `GET /api/providers/{id}`
* `GET /api/providers/name/{name}`
* `GET /api/providers/email/{email}`
* `PUT /api/providers/{id}`
* `PATCH /api/providers/{id}/deactivate`

---

### 📍 Location

* `POST /api/locations`
* `GET /api/locations`
* `GET /api/locations/active`
* `GET /api/locations/{id}`
* `GET /api/locations/type/{type}`
* `GET /api/locations/floor/{floor}`
* `GET /api/locations/search?name=`
* `PUT /api/locations/{id}`
* `PATCH /api/locations/{id}/deactivate`

---

### 📍 State

* `POST /api/states`
* `GET /api/states`
* `GET /api/states/active`
* `GET /api/states/{id}`
* `PUT /api/states/{id}`
* `PATCH /api/states/{id}/deactivate`

---

### 📍 Equipment

* `POST /api/equipments`
* `GET /api/equipments`
* `GET /api/equipments/active`
* `GET /api/equipments/{id}`
* `GET /api/equipments/serie/{serie}`
* `GET /api/equipments/search?name=`
* `GET /api/equipments/area/{areaId}`
* `GET /api/equipments/classification/{classificationId}`
* `GET /api/equipments/provider/{providerId}`
* `GET /api/equipments/state/{stateId}`
* `GET /api/equipments/location/{locationId}`
* `PUT /api/equipments/{id}`
* `PATCH /api/equipments/{id}/deactivate`

---

## ✅ Validaciones y decisiones técnicas

El sistema implementa diversas validaciones para garantizar la integridad de la información:

* Validación de unicidad del número de serie
* Validación de existencia de entidades relacionadas
* Prevención de registros duplicados
* Eliminación lógica mediante el campo `active`
* Validaciones con Jakarta Validation
* Uso de Spring Data JPA
* Respuestas estandarizadas con `ApiResponse`
* Manejo global de errores con `GlobalExceptionHandler`

---

## 🚀 Cómo ejecutar el proyecto

```bash
mvn spring-boot:run
```
