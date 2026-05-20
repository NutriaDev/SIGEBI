# Reports & Audit — `ms-reportsandaudit`

Microservicio central de **reportes, auditoría y snapshots** del ecosistema SIGEBI. Puerto `8087`.

---

## Descripción

Actúa como el **hub de consulta y exportación** de datos provenientes de otros microservicios (equipos, inventario, movimientos, mantenimientos). Toda la data histórica y de reportes se mantiene en **tablas materializadas** (vistas desnormalizadas) que se actualizan de forma asíncrona mediante eventos de Kafka.

Además, gestiona:
- La generación de **reportes técnicos de servicio** en PDF
- El **log de auditoría** de acciones de usuario
- El **snapshot** e **historial de cambios** de equipos médicos

---

## Stack

| Tecnología | Uso |
|---|---|
| Spring Boot 3 + Web | REST API |
| Spring Data JPA | Persistencia (PostgreSQL) |
| Spring Security + JWT | Autenticación y permisos |
| Spring Cloud OpenFeign | Comunicación síncrona con otros microservicios |
| Apache Kafka | Comunicación asíncrona (event-driven) |
| OpenPDF | Generación de reportes PDF |
| Apache POI | Generación de reportes Excel (.xlsx) |
| Eureka Client | Descubrimiento de servicios |

---

## Arquitectura de datos

El módulo **no es dueño de los datos fuente**. Recibe eventos vía Kafka y mantiene tablas locales desnormalizadas para consulta eficiente:

```
equipment-ms ──► Kafka: sigebi-equipment-events ──► equipment_snapshot + equipment_historial
inventory-ms ──► Kafka: sigebi-inventory-events  ──► inventory_report_view
inventory-ms ──► Kafka: sigebi-movement-events   ──► movement_report_view
maintenance-ms ─► Kafka: sigebi-report-events     ──► maintenance_report_view + consolidated_report_view
```

Además, emite eventos propios:

```
reportsandaudit ─► Kafka: sigebi-audit-events          ──► audit_events (misma BD)
reportsandaudit ─► Kafka: sigebi-service-report-events ──► otros servicios
```

---

## Endpoints

### Auditoría (`/api/audit`)

| Método | Ruta | Permiso | Descripción |
|---|---|---|---|
| POST | `/api/audit/log` | `audit.create` | Registrar acción de auditoría |
| GET | `/api/audit/user/{userId}` | `audit.read` | Logs por usuario |
| GET | `/api/audit/module/{module}` | `audit.read` | Logs por módulo |
| GET | `/api/audit/action/{action}` | `audit.read` | Logs por acción |
| POST | `/api/audit/filters` | `audit.read` | Logs con filtros dinámicos |

### Reportes (`/api/reports`)

| Método | Ruta | Permiso | Descripción |
|---|---|---|---|
| POST | `/api/reports` | `report.create` | Crear solicitud de reporte |
| GET | `/api/reports?type=` | `report.read` | Reportes por tipo |
| GET | `/api/reports/user/{userId}` | `report.read` | Reportes por usuario |
| GET | `/api/reports/status/{status}` | `report.read` | Reportes por estado |
| GET | `/api/reports/{id}` | `report.read` | Reporte por ID |
| PATCH | `/api/reports/{id}/status` | `report.update` | Actualizar estado |
| GET | `/api/reports/{id}/executions` | `report.read` | Ejecuciones de un reporte |

#### Vistas de reportes (solo lectura)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/reports/inventory/location/{locationId}` | Inventario por ubicación |
| GET | `/api/reports/inventory/date-range` | Inventario por rango de fechas |
| GET | `/api/reports/movement/equipment/{equipmentId}` | Movimientos por equipo |
| GET | `/api/reports/movement/date-range` | Movimientos por fecha |
| GET | `/api/reports/maintenance/equipment/{equipmentId}` | Mantenimientos por equipo |
| GET | `/api/reports/maintenance/status/{status}` | Mantenimientos por estado |
| GET | `/api/reports/maintenance/date-range` | Mantenimientos por fecha |
| GET | `/api/reports/equipment-snapshot/{equipmentId}` | Snapshot de equipo |
| GET | `/api/reports/equipment-snapshot/location/{locationId}` | Snapshots por ubicación |
| GET | `/api/reports/consolidated/date-range` | Consolidado por fecha |
| GET | `/api/reports/consolidated/equipment/{equipmentId}` | Consolidado por equipo |
| GET | `/api/reports/consolidated/location/{location}` | Consolidado por ubicación |
| GET | `/api/reports/consolidated/filters` | Consolidado con filtros |

#### Exportación

| Método | Ruta | Permiso | Descripción |
|---|---|---|---|
| GET | `/api/reports/export/{reportId}` | `report.export` | Exportar reporte (PDF/Excel/CSV) |
| GET | `/api/reports/export/direct` | `report.export` | Exportación directa sin reporte previo |
| POST | `/api/reports/export/{reportId}/audit-download` | `report.export` | Registrar descarga |

### Reportes técnicos de servicio (`/api/service-reports`)

| Método | Ruta | Permiso | Descripción |
|---|---|---|---|
| POST | `/api/service-reports` | `report.create` | Generar PDF de reporte técnico |

---

## Flujo de datos

### 1. Snapshot e historial de equipos

```
equipment-ms (create/update/delete)
  → Kafka: sigebi-equipment-events
  → EquipmentEventConsumer
    → UPSERT en equipment_snapshot (estado actual)
    → INSERT en equipment_historial (historial de cambios)
```

### 2. Movimientos de inventario

```
inventory-ms
  → Kafka: sigebi-movement-events
  → MovementEventConsumer
    → INSERT en movement_report_view
```

### 3. Inventarios por ubicación

```
inventory-ms
  → Kafka: sigebi-inventory-events
  → InventoryEventConsumer
    → INSERT en inventory_report_view
```

### 4. Mantenimientos y reporte consolidado

```
maintenance-ms
  → Kafka: sigebi-report-events
  → ReportEventConsumer
    → INSERT en maintenance_report_view
    → INSERT en consolidated_report_view
```

### 5. Reporte técnico de servicio (PDF)

```
POST /api/service-reports
  → MaintenanceServiceReportService
    1. Valida mantenimiento (Feign → maintenance-ms)
    2. Obtiene usuario autenticado
    3. Obtiene datos de equipo (Feign → equipment-ms)
    4. Genera PDF con diagnóstico, actividades, repuestos, firmas
    5. Guarda PDF en ./reports/maintenance/
    6. Persiste entidad en BD
    7. Publica evento Kafka: sigebi-service-report-events
    8. Publica evento auditoría: sigebi-audit-events
```

### 6. Exportación de reportes

```
GET /api/reports/export/{reportId}?format=PDF|EXCEL|CSV
  → ReportPermissionValidator (ownership + permisos)
  → ReportExportService.export()
    → fetchData() según ReportType
    → Delega en PdfExportStrategy | ExcelExportStrategy | CsvExportStrategy
  → AuditService.logDownload()
  → bytes[] con Content-Type adecuado
```

### 7. Auditoría

```
POST /api/audit/log
  → AuditService.logAudit()
    → INSERT en audit_logs
    → Kafka: sigebi-audit-events
      → AuditEventConsumer → INSERT en audit_events
```

---

## Entidades (tablas)

| Tabla | Propósito |
|---|---|
| `equipment_snapshot` | Estado actual de cada equipo (una fila por equipo) |
| `equipment_historial` | Historial de cambios de equipos (una fila por evento) |
| `inventory_report_view` | Inventarios tomados por ubicación |
| `movement_report_view` | Movimientos de equipos entre ubicaciones |
| `maintenance_report_view` | Mantenimientos realizados |
| `consolidated_report_view` | Vista consolidada (equipo + mantenimiento + ubicación) |
| `audit_logs` | Log de acciones de usuario |
| `audit_events` | Event store de auditoría (payload completo en JSON) |
| `reports` | Solicitudes de reportes |
| `report_executions` | Ejecuciones de reportes |
| `report_files` | Archivos generados |
| `maintenance_service_reports` | Reportes técnicos de servicio en PDF |

---

## Variables de entorno

| Variable | Default | Descripción |
|---|---|---|
| `DB_HOST` | — | Host de PostgreSQL |
| `DB_PORT` | — | Puerto de PostgreSQL |
| `DB_NAME` | — | Nombre de BD |
| `DB_USERNAME` | — | Usuario de BD |
| `DB_PASSWORD` | — | Password de BD |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Kafka bootstrap servers |
| `JWT_SECRET` | — | Secreto para firmar JWT |

---

## Permisos

| Permiso | Endpoints |
|---|---|
| `audit.create` | POST `/api/audit/log` |
| `audit.read` | GET `/api/audit/*`, POST `/api/audit/filters` |
| `report.create` | POST `/api/reports`, POST `/api/service-reports` |
| `report.read` | GET `/api/reports*` |
| `report.update` | PATCH `/api/reports/{id}/status` |
| `report.export` | GET `/api/reports/export/*`, POST `/api/reports/export/*/audit-download` |
