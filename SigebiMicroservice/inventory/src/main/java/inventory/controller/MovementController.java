package inventory.controller;

import feign.Response;
import inventory.client.EquipmentClient;
import inventory.dto_response.ApiResponse;
import inventory.dto_response.EquipmentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import inventory.dto_request.MovementRequest;
import inventory.service.MovementService;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/movements")
@RequiredArgsConstructor
public class MovementController {

    private final MovementService movementService;

    // ================= CREATE MOVEMENT =================
    @PreAuthorize("hasAuthority('movement.create')")
    @PostMapping
    public ResponseEntity<Map<String, Object>> register(
            @Valid @RequestBody MovementRequest request) {

        movementService.registerMovement(request);

        return ResponseEntity.ok(Map.of(
                "timestamp", LocalDateTime.now(),
                "message", "Movimiento registrado correctamente"
        ));
    }

    // ================= GET EQUIPMENT BY SERIAL =================
    @PreAuthorize("hasAuthority('movement.read')")
    @GetMapping("/equipments/by-serial")
    public ResponseEntity<?> findBySerial(@RequestParam String serial) {

        return ResponseEntity.ok(
                movementService.findEquipmentBySerial(serial)
        );
    }

    @GetMapping("/me")
    public Long getUser() {
        return (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}