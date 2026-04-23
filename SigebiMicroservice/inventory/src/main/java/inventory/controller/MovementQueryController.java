package inventory.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import inventory.dto_response.MovementResponse;
import inventory.dto_response.PagedResponse;
import inventory.service.MovementQueryService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/movements")
@RequiredArgsConstructor
public class MovementQueryController {

    private final MovementQueryService movementQueryService;

    @GetMapping
    public ResponseEntity<PagedResponse<MovementResponse>> list(
            @RequestParam int page,
            @RequestParam int limit,
            @RequestParam(required = false) Long equipmentId,
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return ResponseEntity.ok(
                movementQueryService.getMovements(
                        page, limit, equipmentId, locationId, date));
    }
}