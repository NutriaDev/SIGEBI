package equipment.controller;

import equipment.dto_request.CreateEquipmentRequest;
import equipment.dto_request.UpdateEquipmentRequest;
import equipment.dto_response.EquipmentResponse;
import equipment.dto_response.Response;
import equipment.service.EquipmentService;
import equipment.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/equipments")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    // ================= CREATE =================

    @PostMapping
    public ResponseEntity<Response> createEquipment(
            @Valid @RequestBody CreateEquipmentRequest request) {

        EquipmentResponse equipment = equipmentService.createEquipment(request);

        return ApiResponse.success(
                "Equipment created successfully",
                "The equipment was registered correctly",
                equipment
        );
    }

    // ================= GET ALL =================

    @GetMapping
    public ResponseEntity<Response> getAllEquipments(Pageable pageable) {

        return ApiResponse.success(
                "Equipments retrieved",
                "Paginated equipments list",
                equipmentService.getAllEquipments(pageable)
        );
    }

    // ================= GET ACTIVE =================

    @GetMapping("/active")
    public ResponseEntity<Response> getActiveEquipments(Pageable pageable) {

        return ApiResponse.success(
                "Active equipments retrieved",
                "Paginated active equipments",
                equipmentService.getActiveEquipments(pageable)
        );
    }

    // ================= GET BY ID =================

    @GetMapping("/{id}")
    public ResponseEntity<Response> getEquipmentById(@PathVariable Long id) {

        return ApiResponse.success(
                "Equipment retrieved successfully",
                "Equipment found",
                equipmentService.getEquipmentById(id)
        );
    }

    // ================= GET BY SERIE =================

    @GetMapping("/serie/{serie}")
    public ResponseEntity<Response> getEquipmentBySerie(@PathVariable String serie) {

        return ApiResponse.success(
                "Equipment retrieved successfully",
                "Equipment found by serie",
                equipmentService.getEquipmentBySerie(serie)
        );
    }

    // ================= FILTERS =================

    @GetMapping("/area/{id}")
    public ResponseEntity<Response> getEquipmentsByArea(
            @PathVariable Long id,
            Pageable pageable) {

        return ApiResponse.success(
                "Equipments retrieved",
                "Equipments filtered by area",
                equipmentService.getEquipmentsByArea(id, pageable)
        );
    }

    @GetMapping("/classification/{id}")
    public ResponseEntity<Response> getEquipmentsByClassification(
            @PathVariable Long id,
            Pageable pageable) {

        return ApiResponse.success(
                "Equipments retrieved",
                "Equipments filtered by classification",
                equipmentService.getEquipmentsByClassification(id, pageable)
        );
    }

    @GetMapping("/provider/{id}")
    public ResponseEntity<Response> getEquipmentsByProvider(
            @PathVariable Long id,
            Pageable pageable) {

        return ApiResponse.success(
                "Equipments retrieved",
                "Equipments filtered by provider",
                equipmentService.getEquipmentsByProvider(id, pageable)
        );
    }

    @GetMapping("/state/{id}")
    public ResponseEntity<Response> getEquipmentsByState(
            @PathVariable Long id,
            Pageable pageable) {

        return ApiResponse.success(
                "Equipments retrieved",
                "Equipments filtered by state",
                equipmentService.getEquipmentsByState(id, pageable)
        );
    }

    @GetMapping("/location/{id}")
    public ResponseEntity<Response> getEquipmentsByLocation(
            @PathVariable Long id,
            Pageable pageable) {

        return ApiResponse.success(
                "Equipments retrieved",
                "Equipments filtered by location",
                equipmentService.getEquipmentsByLocation(id, pageable)
        );
    }

    // ================= SEARCH =================

    @GetMapping("/search")
    public ResponseEntity<Response> searchEquipments(
            @RequestParam String name,
            Pageable pageable) {

        return ApiResponse.success(
                "Equipments retrieved",
                "Equipments filtered by name",
                equipmentService.searchEquipmentsByName(name, pageable)
        );
    }

    // ================= UPDATE =================

    @PutMapping("/{id}")
    public ResponseEntity<Response> updateEquipment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEquipmentRequest request) {

        EquipmentResponse updatedEquipment = equipmentService.updateEquipment(id, request);

        return ApiResponse.success(
                "Equipment updated successfully",
                "The equipment was updated correctly",
                updatedEquipment
        );
    }

    // ================= DEACTIVATE =================

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Response> deactivateEquipment(@PathVariable Long id) {

        equipmentService.deactivateEquipment(id);

        return ApiResponse.success(
                "Equipment deactivated successfully",
                "The equipment was deactivated correctly",
                null
        );
    }
}