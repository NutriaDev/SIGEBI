package equipment.controller;

import equipment.dto_request.CreateLocationRequest;
import equipment.dto_request.UpdateLocationRequest;
import equipment.dto_response.LocationResponse;
import equipment.dto_response.Response;
import equipment.service.LocationService;
import equipment.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    // ================= CREATE =================
    @PreAuthorize("hasAuthority('equipment.location.create')")
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
    @PreAuthorize("hasAuthority('equipment.location.read')")
    @GetMapping
    public ResponseEntity<Response> getAllLocations(Pageable pageable) {

        return ApiResponse.success(
                "Locations retrieved",
                "Paginated locations list",
                locationService.getAllLocations(pageable)
        );
    }
    // ================= GET ACTIVE =================
    @PreAuthorize("hasAuthority('equipment.location.read')")
    @GetMapping("/active")
    public ResponseEntity<Response> getActiveLocations(Pageable pageable) {

        return ApiResponse.success(
                "Active locations retrieved",
                "Paginated active locations",
                locationService.getActiveLocations(pageable)
        );
    }

    @GetMapping("/active/all")
    public ResponseEntity<Response> getAllActive() {
        return ApiResponse.success(
                "Ubicaciones activas",
                "Listado completo de ubicaciones activas",
                locationService.getAllActive(true)
        );
    }

    // ================= GET BY ID =================
    @PreAuthorize("hasAuthority('equipment.location.read')")
    @GetMapping("/{id}")
    public ResponseEntity<Response> getLocationById(@PathVariable Long id) {

        return ApiResponse.success(
                "Location retrieved",
                "Location found",
                locationService.getLocationById(id)
        );
    }

    // ================= GET BY NAME =================
    @PreAuthorize("hasAuthority('equipment.location.read')")
    @GetMapping("/name/{name}")
    public ResponseEntity<Response> getLocationByName(@PathVariable String name) {

        return ApiResponse.success(
                "Location retrieved",
                "Location found by name",
                locationService.getLocationByName(name)
        );
    }

    // ================= GET BY TYPE =================
    @PreAuthorize("hasAuthority('equipment.location.read')")
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
    @PreAuthorize("hasAuthority('equipment.location.read')")
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
    @PreAuthorize("hasAuthority('equipment.location.read')")
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
    @PreAuthorize("hasAuthority('equipment.location.update')")
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
    @PreAuthorize("hasAuthority('equipment.location.update')")
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
