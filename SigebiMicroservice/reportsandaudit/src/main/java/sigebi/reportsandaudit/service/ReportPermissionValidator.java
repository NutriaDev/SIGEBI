package sigebi.reportsandaudit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sigebi.reportsandaudit.entities.ReportEntity;
import sigebi.reportsandaudit.exception.PermissionDeniedException;
import sigebi.reportsandaudit.repository.ReportRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ReportPermissionValidator {

    private final ReportRepository reportRepository;

    public void validateOwnership(Long reportId, Long userId) {

        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new PermissionDeniedException("El reporte no existe"));

        if (!report.getCreatedBy().equals(userId)) {
            throw new PermissionDeniedException("No tienes permisos sobre este reporte");
        }
    }

    public void validateCreatePermission(Collection<String> authorities) {

        if (!authorities.contains("report.create")) {
            throw new PermissionDeniedException("No tienes permisos para crear reportes");
        }
    }

    public void validateExportPermission(Long reportId, Long userId, Collection<String> authorities) {

        // 🔥 1. Permiso base
        if (!authorities.contains("report.export")) {
            throw new PermissionDeniedException("No tienes permisos para exportar");
        }

        // 🔥 2. Permiso avanzado (puede exportar de otros)
        if (authorities.contains("report.export.all")) {
            return;
        }

        // 🔥 3. Solo dueño
        validateOwnership(reportId, userId);
    }
}