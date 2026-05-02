package sigebi.reportsandaudit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sigebi.reportsandaudit.dto_request.ReportRequest;
import sigebi.reportsandaudit.dto_response.ReportResponse;
import sigebi.reportsandaudit.entities.*;
import sigebi.reportsandaudit.exception.BusinessException;
import sigebi.reportsandaudit.kafka.AuditEventProducer;
import sigebi.reportsandaudit.kafka.ReportEventProducer;
import sigebi.reportsandaudit.kafka.AuditEvent;
import sigebi.reportsandaudit.kafka.ReportEvent;
import sigebi.reportsandaudit.repository.*;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportFileRepository reportFileRepository;
    private final ReportExecutionRepository reportExecutionRepository;
    private final AuditEventProducer auditEventProducer;
    private final ReportEventProducer reportEventProducer;

    public ReportResponse createReport(ReportRequest request, Long userId) {
        ReportEntity entity = ReportEntity.builder()
                .type(request.getType())
                .format(request.getFormat())
                .createdBy(userId)
                .status(ReportStatus.PENDING)
                .filters(request.getFilters())
                .build();

        ReportEntity saved = reportRepository.save(entity);

        AuditEvent auditEvent = AuditEvent.builder()
                .userId(userId)
                .action("CREATE_REPORT")
                .module("REPORTS")
                .entityId(saved.getId())
                .entityType("ReportEntity")
                .details("Reporte creado: " + request.getType())
                .timestamp(LocalDateTime.now())
                .ipAddress("system")
                .build();
        auditEventProducer.sendAuditEvent(auditEvent);

        ReportEvent reportEvent = ReportEvent.builder()
                .reportId(saved.getId())
                .type(saved.getType().name())
                .format(saved.getFormat().name())
                .createdBy(saved.getCreatedBy())
                .status(saved.getStatus().name())
                .build();
        reportEventProducer.sendReportEvent(reportEvent);

        return mapToResponse(saved);
    }

    public Page<ReportResponse> getReportsByType(ReportType type, Pageable pageable) {
        return reportRepository.findByType(type, pageable).map(this::mapToResponse);
    }

    public Page<ReportResponse> getReportsByUser(Long userId, Pageable pageable) {
        return reportRepository.findByCreatedBy(userId, pageable).map(this::mapToResponse);
    }

    public Page<ReportResponse> getReportsByStatus(ReportStatus status, Pageable pageable) {
        return reportRepository.findByStatus(status, pageable).map(this::mapToResponse);
    }

    public ReportResponse getReportById(Long id) {
        ReportEntity entity = reportRepository.findById(id)
                .orElseThrow(() -> new BusinessException("REPORT_NOT_FOUND", "El reporte no existe"));
        return mapToResponse(entity);
    }

    public ReportResponse updateReportStatus(Long id, ReportStatus status, Long userId) {
        ReportEntity entity = reportRepository.findById(id)
                .orElseThrow(() -> new BusinessException("REPORT_NOT_FOUND", "El reporte no existe"));

        entity.setStatus(status);
        ReportEntity saved = reportRepository.save(entity);

        ReportEvent reportEvent = ReportEvent.builder()
                .reportId(saved.getId())
                .type(saved.getType().name())
                .status(saved.getStatus().name())
                .createdBy(saved.getCreatedBy())
                .build();
        reportEventProducer.sendReportEvent(reportEvent);

        return mapToResponse(saved);
    }

    public void registerExecution(Long reportId, Long executionTime, Long recordsCount,
                                  ReportExecutionEntity.ExecutionStatus execStatus, String errorMessage) {
        ReportExecutionEntity execution = ReportExecutionEntity.builder()
                .reportId(reportId)
                .executionTime(executionTime)
                .recordsCount(recordsCount)
                .status(execStatus)
                .errorMessage(errorMessage)
                .build();
        reportExecutionRepository.save(execution);
    }

    public java.util.List<ReportExecutionEntity> getExecutionsByReport(Long reportId) {
        return reportExecutionRepository.findByReportId(reportId);
    }

    private ReportResponse mapToResponse(ReportEntity e) {
        return ReportResponse.builder()
                .id(e.getId())
                .type(e.getType().name())
                .format(e.getFormat().name())
                .createdBy(e.getCreatedBy())
                .createdAt(e.getCreatedAt())
                .status(e.getStatus().name())
                .filters(e.getFilters())
                .build();
    }
}
