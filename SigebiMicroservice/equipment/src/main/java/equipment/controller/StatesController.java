package equipment.controller;

import equipment.dto_request.CreateStatesRequest;
import equipment.dto_request.UpdateStatesRequest;
import equipment.dto_response.Response;
import equipment.dto_response.StatesResponse;
import equipment.service.StatesService;
import equipment.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/states")
@RequiredArgsConstructor
public class StatesController {

    private final StatesService statesService;

    // ================= CREATE =================
    @PreAuthorize("hasAuthority('equipment.state.create')")
    @PostMapping
    public ResponseEntity<Response> createStatus(
            @Valid @RequestBody CreateStatesRequest request) {

        StatesResponse response = statesService.createStatus(request);

        return ApiResponse.success(
                "State created",
                "State registered successfully",
                response
        );
    }

    // ================= GET ALL =================
    @PreAuthorize("hasAuthority('equipment.state.read')")
    @GetMapping
    public ResponseEntity<Response> getAllStatuses() {

        return ApiResponse.success(
                "States retrieved",
                "All states list",
                statesService.getAllStatuses()
        );
    }

    // ================= GET ACTIVE =================
    @PreAuthorize("hasAuthority('equipment.state.read')")
    @GetMapping("/active")
    public ResponseEntity<Response> getActiveStatuses() {

        return ApiResponse.success(
                "Active states retrieved",
                "Active states list",
                statesService.getActiveStatuses()
        );
    }

    // ================= GET BY ID =================
    @PreAuthorize("hasAuthority('equipment.state.read')")
    @GetMapping("/{id}")
    public ResponseEntity<Response> getStatusById(@PathVariable Long id) {

        return ApiResponse.success(
                "State retrieved",
                "State found",
                statesService.getStatusById(id)
        );
    }

    // ================= GET BY NAME =================
    @PreAuthorize("hasAuthority('equipment.state.read')")
    @GetMapping("/name/{name}")
    public ResponseEntity<Response> getStatusByName(@PathVariable String name) {

        return ApiResponse.success(
                "State retrieved",
                "State found by name",
                statesService.getStatusByName(name)
        );
    }

    // ================= UPDATE =================
    @PreAuthorize("hasAuthority('equipment.state.update')")
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatesRequest request) {

        StatesResponse response = statesService.updateStatus(id, request);

        return ApiResponse.success(
                "State updated",
                "State updated successfully",
                response
        );
    }

    // ================= DEACTIVATE =================
    @PreAuthorize("hasAuthority('equipment.state.update')")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Response> deactivateStatus(@PathVariable Long id) {

        statesService.deactivateStatus(id);

        return ApiResponse.success(
                "State deactivated",
                "State deactivated successfully",
                null
        );
    }
}