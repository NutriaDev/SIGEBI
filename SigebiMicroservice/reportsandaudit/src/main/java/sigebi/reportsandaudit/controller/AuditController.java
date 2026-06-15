package sigebi.reportsandaudit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sigebi.reportsandaudit.dto_request.AuditFilterRequest;
import sigebi.reportsandaudit.dto_request.AuditLogRequest;
import sigebi.reportsandaudit.dto_response.ApiResponse;
import sigebi.reportsandaudit.dto_response.AuditLogResponse;
import sigebi.reportsandaudit.service.AuditService;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    private Long getAuthenticatedUserId() {
        return (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    @PostMapping("/log")
    @PreAuthorize("hasAuthority('audit.create')")
    public ResponseEntity<ApiResponse> logAudit(
            @Valid @RequestBody AuditLogRequest request
    ) {

        Long userId = getAuthenticatedUserId();
        auditService.logAudit(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status("success")
                        .title("Auditoría registrada")
                        .message("Registro de auditoría almacenado correctamente")
                        .body(null)
                        .build()
        );
    }

    @PostMapping("/filters")
    @PreAuthorize("hasAuthority('audit.read')")
    public ResponseEntity<ApiResponse> getLogsWithFilters(
            @RequestBody AuditFilterRequest request
    ) {

        Page<AuditLogResponse> result =
                auditService.getLogsWithFilters(request);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status("success")
                        .title("Logs de auditoría")
                        .message("Consulta realizada correctamente")
                        .body(result)
                        .build()
        );
    }
}