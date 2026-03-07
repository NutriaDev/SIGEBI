package equipment.controller;

import equipment.dto_request.CreateLocationRequest;
import equipment.dto_request.UpdateLocationRequest;
import equipment.dto_response.LocationResponse;
import equipment.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sigebi.users.dto_response.Response;
import sigebi.users.util.ApiResponse;

@Slf4j
@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<Response> createLocation(
            @Valid @RequestBody CreateLocationRequest request) {

        LocationResponse response = locationService.createLocation(request);

        return ApiResponse.success(
                "Location created",
                "Location registered successfully",
                response
        );
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<Response> getAllLocations(Pageable pageable) {

        return ApiResponse.success(
                "Locations retrieved",
                "Paginated locations list",
                locationService.getAllLocations(pageable)
        );
    }
    // ================= GET ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<Response> getActiveLocations(Pageable pageable) {

        return ApiResponse.success(
                "Active locations retrieved",
                "Paginated active locations",
                locationService.getActiveLocations(pageable)
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<Response> getLocationById(@PathVariable Long id) {

        return ApiResponse.success(
                "Location retrieved",
                "Location found",
                locationService.getLocationById(id)
        );
    }

    // ================= GET BY NAME =================
    @GetMapping("/name/{name}")
    public ResponseEntity<Response> getLocationByName(@PathVariable String name) {

        return ApiResponse.success(
                "Location retrieved",
                "Location found by name",
                locationService.getLocationByName(name)
        );
    }

    // ================= GET BY TYPE =================
    @GetMapping("/type/{type}")
    public ResponseEntity<Response> getLocationsByType(
            @PathVariable String type,
            Pageable pageable) {

        return ApiResponse.success(
                "Locations retrieved",
                "Locations by type",
                locationService.getLocationsByType(type, pageable)
        );
    }

    // ================= GET BY FLOOR =================
    @GetMapping("/floor/{floor}")
    public ResponseEntity<Response> getLocationsByFloor(
            @PathVariable String floor,
            Pageable pageable) {

        return ApiResponse.success(
                "Locations retrieved",
                "Locations by floor",
                locationService.getLocationsByFloor(floor, pageable)
        );
    }

    // ================= SEARCH =================
    @GetMapping("/search")
    public ResponseEntity<Response> searchLocationsByName(
            @RequestParam String name,
            Pageable pageable) {

        return ApiResponse.success(
                "Locations retrieved",
                "Search results",
                locationService.searchLocationsByName(name, pageable)
        );
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateLocation(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLocationRequest request) {

        LocationResponse response = locationService.updateLocation(id, request);

        return ApiResponse.success(
                "Location updated",
                "Location updated successfully",
                response
        );
    }

    // ================= DEACTIVATE =================
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Response> deactivateLocation(@PathVariable Long id) {

        locationService.deactivateLocation(id);

        return ApiResponse.success(
                "Location deactivated",
                "Location deactivated successfully",
                null
        );
    }
}
