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

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('audit.read')")
    public ResponseEntity<ApiResponse> getLogsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLogResponse> result = auditService.getLogsByUserId(userId, pageable);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status("success")
                        .title("Logs por usuario")
                        .message("Consulta realizada correctamente")
                        .body(result)
                        .build()
        );
    }

    @GetMapping("/module/{module}")
    @PreAuthorize("hasAuthority('audit.read')")
    public ResponseEntity<ApiResponse> getLogsByModule(
            @PathVariable String module,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLogResponse> result = auditService.getLogsByModule(module, pageable);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status("success")
                        .title("Logs por módulo")
                        .message("Consulta realizada correctamente")
                        .body(result)
                        .build()
        );
    }

    @GetMapping("/action/{action}")
    @PreAuthorize("hasAuthority('audit.read')")
    public ResponseEntity<ApiResponse> getLogsByAction(
            @PathVariable String action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLogResponse> result = auditService.getLogsByAction(action, pageable);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status("success")
                        .title("Logs por acción")
                        .message("Consulta realizada correctamente")
                        .body(result)
                        .build()
        );
    }

    @PostMapping("/filters")
    @PreAuthorize("hasAuthority('audit.read')")
    public ResponseEntity<ApiResponse> getLogsWithFilters(
            @RequestBody AuditFilterRequest request
    ) {
        Page<AuditLogResponse> result = auditService.getLogsWithFilters(request);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status("success")
                        .title("Logs con filtros")
                        .message("Consulta realizada correctamente")
                        .body(result)
                        .build()
        );
    }
}
