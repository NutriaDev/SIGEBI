package sigebi.reportsandaudit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sigebi.reportsandaudit.dto_request.AuditFilterRequest;
import sigebi.reportsandaudit.dto_request.AuditLogRequest;
import sigebi.reportsandaudit.dto_response.AuditLogResponse;
import sigebi.reportsandaudit.entities.AuditLogEntity;
import sigebi.reportsandaudit.kafka.AuditEvent;
import sigebi.reportsandaudit.kafka.AuditEventProducer;
import sigebi.reportsandaudit.repository.AuditLogRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final AuditEventProducer auditEventProducer;

    public void logAudit(AuditLogRequest request, Long userId) {
        AuditLogEntity entity = AuditLogEntity.builder()
                .userId(userId)
                .action(request.getAction())
                .module(request.getModule())
                .entityId(request.getEntityId())
                .entityType(request.getEntityType())
                .details(request.getDetails())
                .timestamp(request.getTimestamp() != null ? request.getTimestamp() : LocalDateTime.now())
                .ipAddress(request.getIpAddress())
                .build();

        auditLogRepository.save(entity);

        AuditEvent event = AuditEvent.builder()
                .userId(userId)
                .action(request.getAction())
                .module(request.getModule())
                .entityId(request.getEntityId())
                .entityType(request.getEntityType())
                .details(request.getDetails())
                .timestamp(entity.getTimestamp())
                .ipAddress(request.getIpAddress())
                .build();

        auditEventProducer.sendAuditEvent(event);
    }

    public Page<AuditLogResponse> getLogsByUserId(Long userId, Pageable pageable) {
        return auditLogRepository.findByUserId(userId, pageable).map(this::mapToResponse);
    }

    public Page<AuditLogResponse> getLogsByModule(String module, Pageable pageable) {
        return auditLogRepository.findByModule(module, pageable).map(this::mapToResponse);
    }

    public void logDownload(Long reportId, String reportType, String format, Long userId, String ipAddress) {
        AuditLogEntity entity = AuditLogEntity.builder()
                .userId(userId)
                .action("DOWNLOAD_REPORT")
                .module("REPORTS")
                .entityId(reportId)
                .entityType("ReportEntity")
                .details("Descarga de reporte tipo=" + reportType + " formato=" + format)
                .timestamp(LocalDateTime.now())
                .ipAddress(ipAddress)
                .build();

        auditLogRepository.save(entity);

        AuditEvent event = AuditEvent.builder()
                .userId(userId)
                .action("DOWNLOAD_REPORT")
                .module("REPORTS")
                .entityId(reportId)
                .entityType("ReportEntity")
                .details("Descarga de reporte tipo=" + reportType + " formato=" + format)
                .timestamp(entity.getTimestamp())
                .ipAddress(ipAddress)
                .build();

        auditEventProducer.sendAuditEvent(event);
    }

    public Page<AuditLogResponse> getLogsByAction(String action, Pageable pageable) {
        return auditLogRepository.findByAction(action, pageable).map(this::mapToResponse);
    }

    public Page<AuditLogResponse> getLogsWithFilters(AuditFilterRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        return auditLogRepository.findWithFilters(
                request.getUserId(),
                request.getModule(),
                request.getAction(),
                request.getFromDate(),
                request.getToDate(),
                pageable
        ).map(this::mapToResponse);
    }

    private AuditLogResponse mapToResponse(AuditLogEntity e) {
        return AuditLogResponse.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .action(e.getAction())
                .module(e.getModule())
                .entityId(e.getEntityId())
                .entityType(e.getEntityType())
                .details(e.getDetails())
                .timestamp(e.getTimestamp())
                .ipAddress(e.getIpAddress())
                .build();
    }
}
