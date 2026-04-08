package sigebi.inventory.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sigebi.inventory.dto_response.MovementResponse;
import sigebi.inventory.dto_response.PagedResponse;
import sigebi.inventory.service.MovementQueryService;

import java.time.LocalDate;

@RestController
@RequestMapping("/movements")
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