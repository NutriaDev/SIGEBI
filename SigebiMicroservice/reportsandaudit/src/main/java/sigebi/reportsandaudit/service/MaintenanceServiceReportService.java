package sigebi.reportsandaudit.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;import org.springframework.stereotype.Service;
import sigebi.reportsandaudit.client.EquipmentClient;
import sigebi.reportsandaudit.client.EquipmentDetail;
import sigebi.reportsandaudit.client.MaintenanceClient;
import sigebi.reportsandaudit.client.MaintenanceDetail;
import sigebi.reportsandaudit.client.UserClient;
import sigebi.reportsandaudit.dto_request.MaintenanceServiceReportRequest;
import sigebi.reportsandaudit.dto_response.MaintenanceServiceReportResponse;
import sigebi.reportsandaudit.entities.MaintenanceServiceReportEntity;
import sigebi.reportsandaudit.exception.BusinessException;
import sigebi.reportsandaudit.kafka.AuditEvent;
import sigebi.reportsandaudit.kafka.AuditEventProducer;
import sigebi.reportsandaudit.kafka.MaintenanceServiceReportCreatedEvent;
import sigebi.reportsandaudit.kafka.ServiceReportEventProducer;
import sigebi.reportsandaudit.repository.MaintenanceServiceReportRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class MaintenanceServiceReportService {

    private final MaintenanceServiceReportRepository repository;
    private final MaintenanceClient maintenanceClient;
    private final EquipmentClient equipmentClient;
    private final UserClient userClient;
    private final ServiceReportPdfGenerator pdfGenerator;
    private final ServiceReportEventProducer eventProducer;
    private final AuditEventProducer auditEventProducer;

    @Value("${reports.maintenance.pdf-directory:./reports/maintenance}")
    private String pdfDirectory;

    public MaintenanceServiceReportResponse createServiceReport(
            MaintenanceServiceReportRequest request, String ipAddress) {

        // 1. Validar que el mantenimiento existe
        MaintenanceDetail maintenance = validateMaintenanceExists(request.getMaintenanceId());

        Long userId = getAuthenticatedUserId();
        String reporterName = getUserFullName(userId);
        EquipmentDetail equipment = getEquipment(maintenance.getEquipmentId());
        String serie = equipment != null ? equipment.getSerie() : "";
        String locationName = equipment != null ? equipment.getLocationName() : "";
        LocalDateTime now = LocalDateTime.now();

        // 2. Generar el PDF ANTES de persistir (evita guardar con pdf_path = null)
        byte[] pdfBytes = pdfGenerator.generate(
                request.getMaintenanceId(),
                request.getDiagnosis(),
                request.getActivitiesPerformed(),
                request.getObservations(),
                request.getSparePartsUsed(),
                maintenance.getTechnicianId(),
                maintenance.getEquipmentId(),
                serie,
                now,
                reporterName,
                maintenance.getMaintenanceType(),
                locationName
        );

        // 3. Guardar el archivo en disco
        String pdfPath = savePdfFile(pdfBytes, request.getMaintenanceId());

        // 4. Construir la entidad con todos los campos completos y hacer UN SOLO save
        MaintenanceServiceReportEntity entity = MaintenanceServiceReportEntity.builder()
                .maintenanceId(request.getMaintenanceId())
                .diagnosis(request.getDiagnosis())
                .activitiesPerformed(request.getActivitiesPerformed())
                .observations(request.getObservations())
                .sparePartsUsed(request.getSparePartsUsed())
                .serialNumber(serie)
                .pdfPath(pdfPath)
                .pdfGeneratedAt(now)
                .createdBy(userId)
                .build();

        MaintenanceServiceReportEntity saved = repository.save(entity);

        // 5. Publicar eventos Kafka
        publishKafkaEvent(saved);
        registerAuditEvent(saved, userId, ipAddress);

        return mapToResponse(saved);
    }

    private MaintenanceDetail validateMaintenanceExists(Long maintenanceId) {
        try {
            var response = maintenanceClient.getMaintenanceById(maintenanceId);
            if (response == null || !"success".equalsIgnoreCase(response.getStatus()) || response.getBody() == null) {
                throw new BusinessException("MAINTENANCE_NOT_FOUND",
                        "El mantenimiento con ID " + maintenanceId + " no existe");
            }
            return response.getBody();
        } catch (FeignException.NotFound e) {
            throw new BusinessException("MAINTENANCE_NOT_FOUND",
                    "El mantenimiento con ID " + maintenanceId + " no existe");
        } catch (FeignException e) {
            throw new BusinessException("MAINTENANCE_SERVICE_ERROR",
                    "Error al conectar con el servicio de mantenimiento");
        }
    }

    private String savePdfFile(byte[] pdfBytes, Long maintenanceId) {
        try {
            File dir = new File(pdfDirectory);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "report_" + maintenanceId + "_" + timestamp + ".pdf";
            File pdfFile = new File(dir, fileName);

            try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
                fos.write(pdfBytes);
            }

            return "/reports/maintenance/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el archivo PDF", e);
        }
    }

    private void publishKafkaEvent(MaintenanceServiceReportEntity entity) {
        MaintenanceServiceReportCreatedEvent event = MaintenanceServiceReportCreatedEvent.builder()
                .maintenanceId(entity.getMaintenanceId())
                .reportId(entity.getId())
                .pdfPath(entity.getPdfPath())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .build();

        eventProducer.sendServiceReportEvent(event);
    }

    private void registerAuditEvent(MaintenanceServiceReportEntity entity, Long userId, String ipAddress) {
        AuditEvent auditEvent = AuditEvent.builder()
                .userId(userId)
                .action("CREATE_MAINTENANCE_SERVICE_REPORT")
                .module("MAINTENANCE_SERVICE_REPORT")
                .entityId(entity.getId())
                .entityType("MaintenanceServiceReportEntity")
                .details("Reporte técnico generado para mantenimiento ID=" + entity.getMaintenanceId()
                        + ", PDF=" + entity.getPdfPath())
                .timestamp(LocalDateTime.now())
                .ipAddress(ipAddress)
                .build();

        auditEventProducer.sendAuditEvent(auditEvent);
    }

    private Long getAuthenticatedUserId() {
        return (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private String getUserFullName(Long userId) {
        try {
            var user = userClient.getUserById(userId);
            return user != null ? user.getFullName() : "N/A";
        } catch (Exception e) {
            return "N/A";
        }
    }

    private EquipmentDetail getEquipment(Long equipmentId) {
        try {
            var response = equipmentClient.getEquipmentById(equipmentId);
            if (response != null && response.getBody() != null) {
                return response.getBody();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private MaintenanceServiceReportResponse mapToResponse(MaintenanceServiceReportEntity entity) {
        return MaintenanceServiceReportResponse.builder()
                .id(entity.getId())
                .maintenanceId(entity.getMaintenanceId())
                .diagnosis(entity.getDiagnosis())
                .activitiesPerformed(entity.getActivitiesPerformed())
                .observations(entity.getObservations())
                .sparePartsUsed(entity.getSparePartsUsed())
                .pdfPath(entity.getPdfPath())
                .digitalSignatureUrl(entity.getDigitalSignatureUrl())
                .pdfGeneratedAt(entity.getPdfGeneratedAt())
                .signedAt(entity.getSignedAt())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }
}