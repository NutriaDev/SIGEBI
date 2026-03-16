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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/equipments")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    // ================= CREATE =================
    @PreAuthorize("hasAuthority('equipment.create')")
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
    @PreAuthorize("hasAuthority('equipment.read')")
    @GetMapping
    public ResponseEntity<Response> getAllEquipments(Pageable pageable) {

        return ApiResponse.success(
                "Equipments retrieved",
                "Paginated equipments list",
                equipmentService.getAllEquipments(pageable)
        );
    }

    // ================= GET ACTIVE =================
    @PreAuthorize("hasAuthority('equipment.read')")
    @GetMapping("/active")
    public ResponseEntity<Response> getActiveEquipments(Pageable pageable) {

        return ApiResponse.success(
                "Active equipments retrieved",
                "Paginated active equipments",
                equipmentService.getActiveEquipments(pageable)
        );
    }

    // ================= GET BY ID =================
    @PreAuthorize("hasAuthority('equipment.read')")
    @GetMapping("/{id}")
    public ResponseEntity<Response> getEquipmentById(@PathVariable Long id) {

        return ApiResponse.success(
                "Equipment retrieved successfully",
                "Equipment found",
                equipmentService.getEquipmentById(id)
        );
    }

    // ================= GET BY SERIE =================
    @PreAuthorize("hasAuthority('equipment.read')")
    @GetMapping("/serie/{serie}")
    public ResponseEntity<Response> getEquipmentBySerie(@PathVariable String serie) {

        return ApiResponse.success(
                "Equipment retrieved successfully",
                "Equipment found by serie",
                equipmentService.getEquipmentBySerie(serie)
        );
    }

    // ================= FILTERS BY NAME =================

    @PreAuthorize("hasAuthority('equipment.read')")
    @GetMapping("/area")
    public ResponseEntity<Response> getEquipmentsByAreaName(
            @RequestParam String name,
            Pageable pageable) {

        return ApiResponse.success(
                "Equipments retrieved",
                "Equipments filtered by area name",
                equipmentService.getEquipmentsByAreaName(name, pageable)
        );
    }

    @PreAuthorize("hasAuthority('equipment.read')")
    @GetMapping("/classification")
    public ResponseEntity<Response> getEquipmentsByClassificationName(
            @RequestParam String name,
            Pageable pageable) {

        return ApiResponse.success(
                "Equipments retrieved",
                "Equipments filtered by classification name",
                equipmentService.getEquipmentsByClassificationName(name, pageable)
        );
    }

    @PreAuthorize("hasAuthority('equipment.read')")
    @GetMapping("/provider")
    public ResponseEntity<Response> getEquipmentsByProviderName(
            @RequestParam String name,
            Pageable pageable) {

        return ApiResponse.success(
                "Equipments retrieved",
                "Equipments filtered by provider name",
                equipmentService.getEquipmentsByProviderName(name, pageable)
        );
    }

    @PreAuthorize("hasAuthority('equipment.read')")
    @GetMapping("/state")
    public ResponseEntity<Response> getEquipmentsByStateName(
            @RequestParam String name,
            Pageable pageable) {

        return ApiResponse.success(
                "Equipments retrieved",
                "Equipments filtered by state name",
                equipmentService.getEquipmentsByStateName(name, pageable)
        );
    }

    @PreAuthorize("hasAuthority('equipment.read')")
    @GetMapping("/location")
    public ResponseEntity<Response> getEquipmentsByLocationName(
            @RequestParam String name,
            Pageable pageable) {

        return ApiResponse.success(
                "Equipments retrieved",
                "Equipments filtered by location name",
                equipmentService.getEquipmentsByLocationName(name, pageable)
        );
    }

    // ================= SEARCH =================
    @PreAuthorize("hasAuthority('equipment.read')")
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
    @PreAuthorize("hasAuthority('equipment.update')")
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
    @PreAuthorize("hasAuthority('equipment.update')")
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