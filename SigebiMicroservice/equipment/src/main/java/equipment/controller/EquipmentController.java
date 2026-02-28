package equipment.controller;

import equipment.constants.ErrorTitles;
import equipment.dto_request.CreateEquipmentRequest;
import equipment.dto_request.UpdateEquipmentRequest;
import equipment.dto_response.EquipmentResponse;
import equipment.dto_response.Response;
import equipment.service.EquipmentService;
import equipment.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/equipments")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @PostMapping
    public ResponseEntity<Response> createEquipment(
            @Valid @RequestBody CreateEquipmentRequest request
    ) {
        try {
            EquipmentResponse equipment = equipmentService.createEquipment(request);
            return ApiResponse.success("Equipment created", "Equipment registered successfully", equipment);

        } catch (Exception e) {
            log.error("Something went wrong while creating equipment: ", e);
            return ApiResponse.internalError(ErrorTitles.INTERNAL_ERROR, e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAllEquipments() {
        try {
            List<EquipmentResponse> equipments = equipmentService.getAllEquipments();
            return ApiResponse.success("Equipments retrieved successfully", "All equipments fetched correctly", equipments);

        } catch (Exception e) {
            log.error("Something went wrong while fetching all equipments: ", e);
            return ApiResponse.internalError(ErrorTitles.INTERNAL_ERROR, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getEquipmentById(@PathVariable Long id) {
        try {
            EquipmentResponse equipment = equipmentService.getEquipmentById(id);

            if (equipment == null) {
                return ApiResponse.notFound(ErrorTitles.EQUIPMENT_NOT_FOUND, "No equipment found with ID: " + id);
            }

            return ApiResponse.success("Equipment found", "Equipment retrieved successfully", equipment);

        } catch (Exception e) {
            log.error("Something went wrong when searching this equipment: ", e);
            return ApiResponse.internalError(ErrorTitles.INTERNAL_ERROR, e.getMessage());
        }
    }

    @GetMapping("/serie/{serie}")
    public ResponseEntity<Response> getEquipmentBySerie(@PathVariable String serie) {
        try {
            EquipmentResponse equipment = equipmentService.getEquipmentBySerie(serie);

            if (equipment == null) {
                return ApiResponse.notFound(ErrorTitles.EQUIPMENT_NOT_FOUND_SERIE, "No equipment found with serie: " + serie);
            }

            return ApiResponse.success("Equipment found", "Equipment retrieved successfully by serie", equipment);

        } catch (Exception e) {
            log.error("Something went wrong when searching equipment by serie: ", e);
            return ApiResponse.internalError(ErrorTitles.INTERNAL_ERROR, e.getMessage());
        }
    }

    @GetMapping("/area/{idArea}")
    public ResponseEntity<Response> getEquipmentsByArea(@PathVariable Long idArea) {
        try {
            List<EquipmentResponse> equipments = equipmentService.getEquipmentsByArea(idArea);
            return ApiResponse.success("Equipments retrieved successfully", "Equipments by area fetched correctly", equipments);

        } catch (Exception e) {
            log.error("Something went wrong while fetching equipments by area: ", e);
            return ApiResponse.internalError(ErrorTitles.INTERNAL_ERROR, e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Response> updateEquipment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEquipmentRequest request
    ) {
        try {
            EquipmentResponse updatedEquipment = equipmentService.updateEquipment(id, request);
            return ApiResponse.success("Equipment updated", "Equipment updated successfully", updatedEquipment);

        } catch (Exception e) {
            log.error("Something went wrong while updating equipment: ", e);
            return ApiResponse.internalError(ErrorTitles.INTERNAL_ERROR, e.getMessage());
        }
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<Response> deactivateEquipment(@PathVariable Long id) {
        try {
            equipmentService.deactivateEquipment(id);
            return ApiResponse.success("Equipment deactivated", "Equipment status updated to inactive", null);

        } catch (Exception e) {
            log.error("Something went wrong while deactivating equipment: ", e);
            return ApiResponse.internalError(ErrorTitles.INTERNAL_ERROR, e.getMessage());
        }
    }
}
