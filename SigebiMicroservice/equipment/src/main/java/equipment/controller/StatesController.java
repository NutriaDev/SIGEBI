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
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/states")
@RequiredArgsConstructor
public class StatesController {

    private final StatesService statesService;

    // ================= CREATE =================
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
    @GetMapping
    public ResponseEntity<Response> getAllStatuses() {

        return ApiResponse.success(
                "States retrieved",
                "All states list",
                statesService.getAllStatuses()
        );
    }

    // ================= GET ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<Response> getActiveStatuses() {

        return ApiResponse.success(
                "Active states retrieved",
                "Active states list",
                statesService.getActiveStatuses()
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<Response> getStatusById(@PathVariable Long id) {

        return ApiResponse.success(
                "State retrieved",
                "State found",
                statesService.getStatusById(id)
        );
    }

    // ================= GET BY NAME =================
    @GetMapping("/name/{name}")
    public ResponseEntity<Response> getStatusByName(@PathVariable String name) {

        return ApiResponse.success(
                "State retrieved",
                "State found by name",
                statesService.getStatusByName(name)
        );
    }

    // ================= UPDATE =================
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