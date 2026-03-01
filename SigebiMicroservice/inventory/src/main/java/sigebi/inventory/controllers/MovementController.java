package sigebi.inventory.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sigebi.inventory.dto.MovementRequest;
import sigebi.inventory.services.MovementService;

@RestController
@RequestMapping("/movements")
@RequiredArgsConstructor
public class MovementController {

    private final MovementService movementService;

    @PostMapping
    public ResponseEntity<String> register(@Valid @RequestBody MovementRequest request) {
        movementService.registerMovement(request);
        return ResponseEntity.ok("Movimiento registrado correctamente");
    }
}
