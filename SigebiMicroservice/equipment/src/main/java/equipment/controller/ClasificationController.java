package equipment.controller;

import equipment.dto_request.CreateClassificationRequest;
import equipment.dto_request.UpdateClassificationRequest;
import equipment.dto_response.Response;
import equipment.service.ClassificationService;
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
@RequestMapping("/api/classifications")
@RequiredArgsConstructor
public class ClasificationController {

    private final ClassificationService classificationService;

    // ================= CREATE =================
    @PreAuthorize("hasAuthority('equipment.classification.create')")
    @PostMapping
    public ResponseEntity<Response> createClassification(
            @Valid @RequestBody CreateClassificationRequest request) {

        return ApiResponse.success(
                "Classification created",
                "Classification registered successfully",
                classificationService.createClassification(request)
        );
    }

    // ================= GET ALL =================
    @PreAuthorize("hasAuthority('equipment.classification.read')")
    @GetMapping
    public ResponseEntity<Response> getAllClassifications(Pageable pageable) {

        return ApiResponse.success(
                "Classifications retrieved",
                "Paginated classifications",
                classificationService.getAllClassifications(pageable)
        );
    }

    // ================= GET ACTIVE =================
    @PreAuthorize("hasAuthority('equipment.classification.read')")
    @GetMapping("/active")
    public ResponseEntity<Response> getActiveClassifications(Pageable pageable, Boolean active) {

        return ApiResponse.success(
                "Active classifications retrieved",
                "Active classifications list",
                classificationService.getActiveClassifications(active, pageable)
        );
    }

    // ================= GET BY ID =================
    @PreAuthorize("hasAuthority('equipment.classification.read')")
    @GetMapping("/{id}")
    public ResponseEntity<Response> getClassificationById(@PathVariable Long id) {

        return ApiResponse.success(
                "Classification retrieved",
                "Classification found",
                classificationService.getClassificationById(id)
        );
    }

    // ================= GET BY NAME =================
    @PreAuthorize("hasAuthority('equipment.classification.read')")
    @GetMapping("/name/{name}")
    public ResponseEntity<Response> getClassificationByName(@PathVariable String name) {

        return ApiResponse.success(
                "Classification retrieved",
                "Classification found by name",
                classificationService.getClassificationByName(name)
        );
    }

    // ================= UPDATE =================
    @PreAuthorize("hasAuthority('equipment.classification.update')")
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateClassification(
            @PathVariable Long id,
            @Valid @RequestBody UpdateClassificationRequest request) {

        return ApiResponse.success(
                "Classification updated",
                "Classification updated successfully",
                classificationService.updateClassification(id, request)
        );
    }

    // ================= DEACTIVATE =================
    @PreAuthorize("hasAuthority('equipment.classification.update')")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Response> deactivateClassification(@PathVariable Long id) {

        classificationService.deactivateClassification(id);

        return ApiResponse.success(
                "Classification deactivated",
                "Classification deactivated successfully",
                null
        );
    }
}
