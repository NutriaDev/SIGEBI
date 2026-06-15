# 📊 ms-reportsandaudit — Reportes y Auditoría

Microservicio central de **reportes, auditoría y snapshots de equipos** del ecosistema SIGEBI. Actúa como hub de consulta y exportación procesando datos de forma asíncrona vía Kafka.

---

## 📌 Descripción

Actúa como el **hub de consulta y exportación** de datos provenientes de otros microservicios (equipos, inventario, movimientos, mantenimientos). Toda la data histórica y de reportes se mantiene en **tablas materializadas** (vistas desnormalizadas) que se actualizan de forma asíncrona mediante eventos de Kafka, siguiendo una **arquitectura orientada a eventos (EDA)**.

**Responsabilidades clave:**
- Gestión de reportes programados (PDF, Excel, CSV)
- Registro centralizado de auditoría de acciones de usuario
- Snapshots e historial de cambios de equipos médicos
- Generación de reportes técnicos de servicio en PDF
- Exportación de reportes en múltiples formatos mediante patrón Strategy

---

## 🏗️ Arquitectura

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Build | Maven |
| Framework | Spring Boot 3.x |
| Base de datos | PostgreSQL |
| Seguridad | Spring Security + JWT |
| Mensajería | Apache Kafka (event-driven) |
| Comunicación síncrona | Spring Cloud OpenFeign |
| Registro | Eureka Client |
| Gateway | Spring Cloud Gateway |
| Reportes PDF | OpenPDF |
| Reportes Excel | Apache POI |

### Capas principales

```
Controller        → Manejo de solicitudes HTTP y autorización
DTO Request       → Validación de datos de entrada (Jakarta Validation)
Service           → Lógica de negocio, exportación y generación de reportes
Repository        → Acceso a datos con Spring Data JPA
Entity            → Modelado de la base de datos
Kafka Consumer    → Procesamiento asíncrono de eventos entrantes
Kafka Producer    → Emisión de eventos de auditoría y reportes
Client            → Feign clients para integración síncrona con otros servicios
Exception         → Manejo global de errores
Security          → Filtro JWT y configuración de seguridad
```

### EDA — Event-Driven Architecture con Apache Kafka

El módulo está diseñado bajo una **arquitectura orientada a eventos (EDA)**. Ningún microservicio fuente se comunica con `ms-reportsandaudit` de forma síncrona para escribir datos; en su lugar, todos **publican eventos a Kafka** cuando ocurre un cambio, y este módulo **consume esos eventos de forma asíncrona** para actualizar sus tablas locales.

Este enfoque **desacopla completamente** a los productores de los consumidores:
- Los microservicios fuente (equipment, inventory, maintenance) emiten eventos sin conocer quién los escucha
- `ms-reportsandaudit` procesa los eventos cuando puede, sin bloquear a los productores
- Si el módulo de reportes cae, los eventos se acumulan en Kafka y se procesan al recuperarse (`auto-offset-reset: earliest`)

#### Diagrama de comunicación asíncrona (Kafka)

```
                    ┌─────────────────────────┐
                    │     equipment-ms        │
                    │  (Equipos médicos)       │
                    └──────────┬──────────────┘
                               │ EquipmentEventProducer
                               ▼
                    ┌─────────────────────┐
                    │sigebi-equipment-events│  ← Topic (3 particiones)
                    └──────────┬──────────────┘
                               │
                               ▼
                    ┌─────────────────────────┐
                    │  EquipmentEventConsumer  │
                    │   → equipment_snapshot   │
                    │   → equipment_historial  │
                    └─────────────────────────┘


                    ┌─────────────────────────┐
                    │     inventory-ms        │
                    │  (Inventario)            │
                    └──────┬──────────┬───────┘
                           │          │
               InventoryEventProducer │ MovementEventProducer
                           │          │
                           ▼          ▼
              ┌──────────────┐  ┌───────────────┐
              │sigebi-inv.   │  │sigebi-movement│
              │events        │  │events         │
              └──────┬───────┘  └───────┬───────┘
                     │                  │
                     ▼                  ▼
              ┌──────────────┐  ┌───────────────┐
              │InventoryEv.  │  │MovementEv.    │
              │Consumer      │  │Consumer       │
              │→ inv_report  │  │→ mov_report   │
              │  _view       │  │  _view        │
              └──────────────┘  └───────────────┘


                    ┌─────────────────────────┐
                    │    maintenance-ms       │
                    │  (Mantenimientos)        │
                    └──────────┬──────────────┘
                               │ ReportEventProducer
                               ▼
                    ┌─────────────────────┐
                    │sigebi-report-events  │
                    └──────────┬──────────────┘
                               │
                               ▼
                    ┌─────────────────────────┐
                    │  ReportEventConsumer     │
                    │   → consolidated_view    │
                    │   → maint_report_view    │
                    │   → equip_snapshot       │
                    └─────────────────────────┘


                    ┌─────────────────────────┐
                    │    reportsandaudit      │  ← Este microservicio
                    │  (Reportes y Auditoría)  │
                    └──────┬──────────┬───────┘
                           │          │
                 ReportEvent    AuditEvent
                 Producer       Producer
                           │          │
                           ▼          ▼
              ┌──────────────┐  ┌───────────────┐
              │sigebi-report │  │sigebi-audit   │
              │events        │  │events         │
              └──────────────┘  └───────┬───────┘
                                        │
                                        ▼
                               ┌───────────────┐
                               │AuditEventCons.│
                               │→ audit_events │
                               └───────────────┘
```

#### Productores de eventos (emiten desde este módulo)

| Producer | Topic | Cuándo se dispara |
|---|---|---|
| `ReportEventProducer` | `sigebi-report-events` | Creación/actualización de reportes y service reports |
| `AuditEventProducer` | `sigebi-audit-events` | Auditoría de acciones de usuario y descargas |
| `ServiceReportEventProducer` | `sigebi-service-report-events` | Generación de PDF de reporte técnico |

#### Consumidores de eventos (escuchan en este módulo)

| Consumer | Topic | Acción |
|---|---|---|
| `EquipmentEventConsumer` | `sigebi-equipment-events` | UPSERT en `equipment_snapshot` + INSERT en `equipment_historial` |
| `InventoryEventConsumer` | `sigebi-inventory-events` | INSERT en `inventory_report_view` |
| `MovementEventConsumer` | `sigebi-movement-events` | INSERT en `movement_report_view` |
| `ReportEventConsumer` | `sigebi-report-events` | INSERT en `consolidated_report_view` + `maintenance_report_view` + actualiza `equipment_snapshot` |
| `AuditEventConsumer` | `sigebi-audit-events` | INSERT en `audit_events` (event store con payload completo) |

#### Group ID del consumidor

```
sigebi-report-group
```

### Strategy — Exportación de reportes

La exportación implementa el patrón **Strategy** para soportar múltiples formatos sin modificar la lógica de negocio:

```
ReportExportService.export(format, type, filters)
  → Obtiene la estrategia según el formato:
      "PDF"   → PdfExportStrategy   (OpenPDF)
      "EXCEL" → ExcelExportStrategy (Apache POI)
      "CSV"   → CsvExportStrategy
  → Cada estrategia implementa ReportExportStrategy:
      byte[] export(List<String> headers, List<List<String>> rows)
      String getContentType()
      String getFileExtension()
```

### Flujo de datos

#### 1. Snapshot e historial de equipos

```
equipment-ms (create/update/delete equipment)
  → EquipmentEventProducer
    → Kafka: sigebi-equipment-events
      → EquipmentEventConsumer
        → UPSERT equipment_snapshot (estado actual del equipo)
        → INSERT equipment_historial (historial de cambios)
```

#### 2. Movimientos de inventario

```
inventory-ms (movimiento de equipo entre ubicaciones)
  → MovementEventProducer
    → Kafka: sigebi-movement-events
      → MovementEventConsumer
        → INSERT movement_report_view
```

#### 3. Inventarios por ubicación

```
inventory-ms (toma de inventario)
  → InventoryEventProducer
    → Kafka: sigebi-inventory-events
      → InventoryEventConsumer
        → INSERT inventory_report_view
```

#### 4. Mantenimientos y reporte consolidado

```
maintenance-ms (creación/actualización de mantenimiento)
  → ReportEventProducer
    → Kafka: sigebi-report-events
      → ReportEventConsumer
        → INSERT maintenance_report_view
        → INSERT consolidated_report_view (equipo + mantenimiento + ubicación)
        → UPDATE equipment_snapshot.last_maintenance_date
```

#### 5. Reporte técnico de servicio (PDF)

```
POST /api/service-reports
  → MaintenanceServiceReportService
    1. Valida mantenimiento (Feign → maintenance-ms)
    2. Obtiene usuario autenticado (JWT)
    3. Obtiene datos de equipo (Feign → equipment-ms)
    4. Genera PDF con OpenPDF (diagnóstico, actividades, repuestos, firmas)
    5. Guarda PDF en ./reports/maintenance/
    6. Persiste entidad en BD (maintenance_service_reports)
    7. Publica Kafka: sigebi-report-events (actualiza consolidated_view)
    8. Publica Kafka: sigebi-service-report-events
    9. Publica Kafka: sigebi-audit-events (auditoría)
```

#### 6. Exportación de reportes

```
GET /api/reports/export/{reportId}?format=PDF|EXCEL|CSV
  → ReportPermissionValidator (ownership + permisos)
  → ReportExportService.export()
    → fetchData() según ReportType
    → Delega en PdfExportStrategy | ExcelExportStrategy | CsvExportStrategy
  → AuditService.logDownload() + AuditEventProducer
  → bytes[] con Content-Type adecuado
```

#### 7. Auditoría

```
POST /api/audit/log
  → AuditService.logAudit()
    → INSERT audit_logs
    → AuditEventProducer → Kafka: sigebi-audit-events
      → AuditEventConsumer → INSERT audit_events (raw JSON payload)
```

---

## 🔐 Seguridad

Cada request debe incluir un token JWT válido:

```
Authorization: Bearer <token>
```

| Permiso | Endpoints |
|---|---|
| `audit.create` | POST `/api/audit/log` |
| `audit.read` | GET `/api/audit/*`, POST `/api/audit/filters` |
| `report.create` | POST `/api/reports`, POST `/api/service-reports` |
| `report.read` | GET `/api/reports*` |
| `report.update` | PATCH `/api/reports/{id}/status` |
| `report.export` | GET `/api/reports/export/*`, POST `/api/reports/export/*/audit-download` |

---

## 🌐 Puerto

```
8087
```

---

## ⚙️ Variables de entorno

| Variable | Default | Descripción |
|---|---|---|
| `DB_HOST` | — | Host de PostgreSQL |
| `DB_PORT` | — | Puerto de PostgreSQL |
| `DB_NAME` | — | Nombre de la base de datos |
| `DB_USERNAME` | — | Usuario de la base de datos |
| `DB_PASSWORD` | — | Contraseña de la base de datos |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Servidores de Kafka (bootstrap) |
| `JWT_SECRET` | — | Clave secreta para firmar/validar JWT |

---

## 🗄️ Modelo de datos

### Entidades principales

#### Reportes

```
Report
├── type: INVENTORY | MAINTENANCE | MOVEMENTS | AUDIT
├── format: PDF | EXCEL | CSV
├── status: PENDING | GENERATED | FAILED
├── created_by (FK → users-ms)
└── filters (JSON)

ReportFile
└── report_id (FK → Report)

ReportExecution
├── report_id (FK → Report)
├── execution_time (ms)
├── records_count
├── status: SUCCESS | FAILED
└── error_message
```

#### Auditoría

```
AuditLog
├── user_id
├── action
├── module
├── entity_id
├── entity_type
├── details (TEXT)
├── ip_address
└── timestamp

AuditEvent (event store)
├── event_type
└── payload (JSON)

AuditAction
└── name (UNIQUE)
```

#### Reporte técnico de servicio

```
MaintenanceServiceReport
├── serial_number
├── maintenance_id (FK → maintenance-ms)
├── diagnosis (VARCHAR 1000)
├── activities_performed (VARCHAR 2000)
├── observations (VARCHAR 1000)
├── spare_parts_used (JSON — lista de SparePartItem)
├── pdf_path
├── digital_signature_url
├── pdf_generated_at
├── signed_at (nullable)
├── created_by
└── created_at
```

#### Vistas materializadas (actualizadas vía Kafka)

```
EquipmentSnapshot (una fila por equipo)
├── equipment_id (PK)
├── name, serial, brand, model
├── location_id, location_name
├── state, classification
├── risk_level
└── last_maintenance_date

EquipmentHistorial (historial de cambios)
├── id (PK)
├── equipment_id, event_type
├── name, serial, brand, model
├── location_id, location_name
├── state_name, classification_name
├── risk_level
├── updated_by
└── timestamp

InventoryReportView
├── inventory_id (PK)
├── location_id, location_name
├── responsible_name
├── date
├── total_equipments
├── active_equipments
└── inactive_equipments

MovementReportView
├── movement_id (PK)
├── equipment_id
├── origin_location_id, destination_location_id
├── date
└── responsible_user_name

MaintenanceReportView
├── maintenance_id (PK)
├── equipment_id
├── type, status
├── date
└── technician_name

ConsolidatedReportView (vista unificada)
├── id (PK)
├── date, equipment_id, equipment_name
├── brand, model, serial, inventory_code
├── physical_location, process_location
├── maintenance_type, maintenance_status
├── observations
├── maintenance_id
└── service_report_id
```

### Relaciones entre servicios

```
ms-equipment ──Kafka──► equipment_snapshot ──┐
ms-inventory ──Kafka──► inventory/movement   ├──► consolidated_report_view
ms-maintenance ─Kafka──► maintenance_report  ──┘
```

---

## 🔌 Endpoints

### Auditoría — `/api/audit`

| Método | Ruta | Permiso | Descripción |
|---|---|---|---|---|
| `POST` | `/api/audit/log` | `audit.create` | Registrar acción de auditoría |
| `POST` | `/api/audit/filters` | `audit.read` | Logs con filtros dinámicos |

### Reportes — `/api/reports`

| Método | Ruta | Permiso | Descripción |
|---|---|---|---|
| `POST` | `/api/reports` | `report.create` | Crear solicitud de reporte |
| `GET` | `/api/reports?type=` | `report.read` | Reportes por tipo |
| `GET` | `/api/reports/user/{userId}` | `report.read` | Reportes por usuario |
| `GET` | `/api/reports/status/{status}` | `report.read` | Reportes por estado |
| `GET` | `/api/reports/{id}` | `report.read` | Reporte por ID |
| `PATCH` | `/api/reports/{id}/status` | `report.update` | Actualizar estado del reporte |
| `GET` | `/api/reports/{id}/executions` | `report.read` | Ejecuciones de un reporte |

#### Vistas de reportes (solo lectura)

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/reports/inventory/location/{locationId}` | Inventario por ubicación |
| `GET` | `/api/reports/inventory/date-range` | Inventario por rango de fechas |
| `GET` | `/api/reports/movement/equipment/{equipmentId}` | Movimientos por equipo |
| `GET` | `/api/reports/movement/date-range` | Movimientos por fecha |
| `GET` | `/api/reports/maintenance/equipment/{equipmentId}` | Mantenimientos por equipo |
| `GET` | `/api/reports/maintenance/status/{status}` | Mantenimientos por estado |
| `GET` | `/api/reports/maintenance/date-range` | Mantenimientos por fecha |
| `GET` | `/api/reports/equipment-snapshot/{equipmentId}` | Snapshot actual de equipo |
| `GET` | `/api/reports/equipment-snapshot/location/{locationId}` | Snapshots por ubicación |
| `GET` | `/api/reports/consolidated/date-range` | Consolidado por fecha |
| `GET` | `/api/reports/consolidated/equipment/{equipmentId}` | Consolidado por equipo |
| `GET` | `/api/reports/consolidated/location/{location}` | Consolidado por ubicación |
| `GET` | `/api/reports/consolidated/filters` | Consolidado con filtros dinámicos |

#### Exportación

| Método | Ruta | Permiso | Descripción |
|---|---|---|---|
| `GET` | `/api/reports/export/{reportId}` | `report.export` | Exportar reporte (PDF/Excel/CSV) |
| `GET` | `/api/reports/export/direct` | `report.export` | Exportación directa sin reporte previo |
| `POST` | `/api/reports/export/{reportId}/audit-download` | `report.export` | Registrar descarga de exportación |

### Reportes técnicos de servicio — `/api/service-reports`

| Método | Ruta | Permiso | Descripción |
|---|---|---|---|
| `POST` | `/api/service-reports` | `report.create` | Generar PDF de reporte técnico de servicio |

---

## 📦 Paginación

Los endpoints `GET` de listado soportan paginación mediante query params de Spring Data:

```
GET /api/reports?type=MAINTENANCE&page=0&size=10&sort=createdAt,desc
```

| Parámetro | Descripción | Default |
|---|---|---|
| `page` | Número de página | `0` |
| `size` | Elementos por página | `10` |
| `sort` | Campo y dirección de ordenamiento | — |

---

## ✅ Validaciones y decisiones técnicas

| Validación/Decisión | Descripción |
|---|---|
| Arquitectura EDA | Los datos fuente nunca se escriben de forma síncrona; todo llega vía eventos Kafka |
| Tablas materializadas | Vistas desnormalizadas actualizadas por consumidores Kafka para consultas eficientes |
| Desacoplamiento total | Los microservicios fuente no conocen a los consumidores; los eventos se acumulan en Kafka si el consumidor cae |
| Strategy Pattern | Exportación en PDF/Excel/CSV mediante estrategias intercambiables sin modificar la lógica de negocio |
| Múltiples formatos de exportación | PDF (OpenPDF), Excel (Apache POI), CSV |
| Reportes autogenerados | El consumidor de eventos actualiza automáticamente las tablas de reportes sin intervención manual |
| Validación de permisos | `ReportPermissionValidator` verifica ownership del reporte + permiso antes de exportar |
| Respuestas estandarizadas | Todas las respuestas usan `ApiResponse` con estructura uniforme |
| Manejo global de errores | `GlobalExceptionHandler` maneja `404`, `400`, `403`, `500` y excepciones de negocio |
| Auditoría en dos capas | `audit_logs` (estructurado) + `audit_events` (event store con payload JSON completo) |
| Type mapping en Kafka | Los eventos de otros microservicios se mapean a clases locales mediante `spring.json.type.mapping` |
| Consumer group | Todos los consumidores comparten `sigebi-report-group` para balanceo de carga |

---

## 🔗 Integraciones

| Servicio | Tipo | Descripción |
|---|---|---|
| **ms-equipment** | Kafka + Feign | Consume eventos de equipos (`sigebi-equipment-events`) para snapshots/historial; consulta datos vía Feign para reportes técnicos |
| **ms-inventory** | Kafka | Consume eventos de inventario (`sigebi-inventory-events`) y movimientos (`sigebi-movement-events`) |
| **ms-maintenance** | Kafka + Feign | Consume eventos de mantenimientos (`sigebi-report-events`) para vistas consolidadas; valida mantenimientos vía Feign |
| **ms-auth** | JWT | Valida tokens JWT en cada request y extrae datos del usuario autenticado |
| **Eureka** | Registro | Registro y descubrimiento de servicios (puerto `8761`) |
| **Gateway** | Proxy | Punto de entrada único en puerto `8080` con rutas hacia `ms-reports-and-audit` y `reports-pdf` |

---

## 🚀 Cómo ejecutar

```bash
mvn spring-boot:run
```

Con variables de entorno explícitas:

```bash
DB_HOST=localhost DB_PORT=5432 DB_NAME=reports_audit DB_USERNAME=user DB_PASSWORD=pass KAFKA_BOOTSTRAP_SERVERS=localhost:9092 JWT_SECRET=secret mvn spring-boot:run
```

> **Nota:** Requiere Kafka y Zookeeper corriendo. Puedes levantarlos con `docker-compose -f docker-compose.kafka.yml up -d` desde la raíz del proyecto.

---

## 📁 Estructura del proyecto

```
reportsandaudit/
├── client/
│   ├── EquipmentClient.java
│   ├── EquipmentDetail.java
│   ├── MaintenanceClient.java
│   ├── MaintenanceDetail.java
│   ├── MaintenanceServiceResponse.java
│   └── UserClient.java
├── config/
│   ├── FeignConfig.java
│   ├── KafkaTopicConfig.java
│   └── WebConfig.java
├── controller/
│   ├── AuditController.java
│   ├── MaintenanceServiceReportController.java
│   └── ReportController.java
├── dto_request/
│   ├── AuditFilterRequest.java
│   ├── AuditLogRequest.java
│   ├── MaintenanceServiceReportRequest.java
│   ├── ReportRequest.java
│   └── SparePartItem.java
├── dto_response/
│   ├── ApiResponse.java
│   ├── AuditLogResponse.java
│   ├── EquipmentApiResponse.java
│   ├── ErrorResponse.java
│   ├── MaintenanceServiceReportResponse.java
│   ├── ReportResponse.java
│   └── UserAuthDataResponse.java
├── entities/
│   ├── AuditActionEntity.java
│   ├── AuditEventEntity.java
│   ├── AuditLogEntity.java
│   ├── ConsolidatedReportViewEntity.java
│   ├── EquipmentHistorialEntity.java
│   ├── EquipmentSnapshotEntity.java
│   ├── InventoryReportViewEntity.java
│   ├── MaintenanceReportViewEntity.java
│   ├── MaintenanceServiceReportEntity.java
│   ├── MovementReportViewEntity.java
│   ├── ReportEntity.java
│   ├── ReportExecutionEntity.java
│   ├── ReportFileEntity.java
│   ├── ReportFormat.java (enum)
│   ├── ReportStatus.java (enum)
│   ├── ReportType.java (enum)
│   └── SparePartsListConverter.java
├── exception/
│   ├── BusinessException.java
│   ├── EmptyResultException.java
│   ├── GlobalExceptionHandler.java
│   ├── PermissionDeniedException.java
│   └── ReportTooLargeException.java
├── kafka/
│   ├── AuditEvent.java
│   ├── AuditEventConsumer.java
│   ├── AuditEventProducer.java
│   ├── EquipmentEvent.java
│   ├── EquipmentEventConsumer.java
│   ├── InventoryEvent.java
│   ├── InventoryEventConsumer.java
│   ├── MaintenanceServiceReportCreatedEvent.java
│   ├── MovementEvent.java
│   ├── MovementEventConsumer.java
│   ├── ReportEvent.java
│   ├── ReportEventConsumer.java
│   ├── ReportEventProducer.java
│   └── ServiceReportEventProducer.java
├── repository/
│   ├── AuditEventRepository.java
│   ├── AuditLogRepository.java
│   ├── ConsolidatedReportViewRepository.java
│   ├── EquipmentHistorialRepository.java
│   ├── EquipmentSnapshotRepository.java
│   ├── InventoryReportViewRepository.java
│   ├── MaintenanceReportViewRepository.java
│   ├── MaintenanceServiceReportRepository.java
│   ├── MovementReportViewRepository.java
│   ├── ReportExecutionRepository.java
│   ├── ReportFileRepository.java
│   └── ReportRepository.java
├── security/
│   ├── JwtAuthorizationFilter.java
│   ├── JwtUtils.java
│   └── SecurityConfig.java
└── service/
    ├── AuditService.java
    ├── CsvExportStrategy.java
    ├── ExcelExportStrategy.java
    ├── MaintenanceServiceReportService.java
    ├── PdfExportStrategy.java
    ├── ReportExportService.java
    ├── ReportExportStrategy.java (interface)
    ├── ReportPermissionValidator.java
    ├── ReportService.java
    ├── ReportViewService.java
    └── ServiceReportPdfGenerator.java
```
