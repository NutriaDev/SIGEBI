package equipment.controller;

import equipment.dto_request.CreateAreaRequest;
import equipment.dto_request.UpdateAreaRequest;
import equipment.dto_response.AreaResponse;
import equipment.service.AreaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sigebi.users.dto_response.Response;
import sigebi.users.util.ApiResponse;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/equipments-area")
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<Response> createArea(
            @Valid @RequestBody CreateAreaRequest request) {

        AreaResponse areaResponse = areaService.createArea(request);

        return ApiResponse.success(
                "Area created successfully",
                "The area was registered correctly",
                areaResponse
        );
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<Response> getAllAreas() {

        List<AreaResponse> areas = areaService.getAllAreas();

        return ApiResponse.success(
                "Areas retrieved successfully",
                "All areas list",
                areas
        );
    }

    // ================= GET ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<Response> getActiveAreas() {

        List<AreaResponse> areas = areaService.getActiveAreas();

        return ApiResponse.success(
                "Active areas retrieved successfully",
                "Active areas list",
                areas
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<Response> getAreaById(@PathVariable Long id) {

        AreaResponse area = areaService.getAreaById(id);

        return ApiResponse.success(
                "Area retrieved successfully",
                "Area found",
                area
        );
    }

    // ================= GET BY NAME =================
    @GetMapping("/name/{name}")
    public ResponseEntity<Response> getAreaByName(@PathVariable String name) {

        AreaResponse area = areaService.getAreaByName(name);

        return ApiResponse.success(
                "Area retrieved successfully",
                "Area found by name",
                area
        );
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateArea(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAreaRequest request) {

        AreaResponse updatedArea = areaService.updateArea(id, request);

        return ApiResponse.success(
                "Area updated successfully",
                "The area was updated correctly",
                updatedArea
        );
    }

    // ================= DEACTIVATE =================
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Response> deactivateArea(@PathVariable Long id) {

        areaService.deactivateArea(id);

        return ApiResponse.success(
                "Area deactivated successfully",
                "The area was deactivated correctly",
                null
        );
    }
}