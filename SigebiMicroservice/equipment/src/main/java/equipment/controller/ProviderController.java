package equipment.controller;

import equipment.dto_request.CreateProviderRequest;
import equipment.dto_request.UpdateProviderRequest;
import equipment.dto_response.ProviderResponse;
import equipment.service.ProviderService;
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
@RequestMapping("/api/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<Response> createProvider(
            @Valid @RequestBody CreateProviderRequest request) {

        ProviderResponse provider = providerService.createProvider(request);

        return ApiResponse.success(
                "Provider created",
                "Provider registered successfully",
                provider
        );
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<Response> getAllProviders(Pageable pageable) {

        return ApiResponse.success(
                "Providers retrieved",
                "Paginated providers list",
                providerService.getAllProviders(pageable)
        );
    }

    // ================= GET ACTIVE =================
    @GetMapping("/active")
    public ResponseEntity<Response> getActiveProviders(Pageable pageable) {

        return ApiResponse.success(
                "Active providers retrieved",
                "Paginated active providers",
                providerService.getActiveProviders(pageable)
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<Response> getProviderById(@PathVariable Long id) {

        return ApiResponse.success(
                "Provider retrieved",
                "Provider found",
                providerService.getProviderById(id)
        );
    }

    // ================= GET BY NAME =================
    @GetMapping("/name/{name}")
    public ResponseEntity<Response> getProviderByName(@PathVariable String name) {

        return ApiResponse.success(
                "Provider retrieved",
                "Provider found by name",
                providerService.getProviderByName(name)
        );
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateProvider(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProviderRequest request) {

        ProviderResponse provider = providerService.updateProvider(id, request);

        return ApiResponse.success(
                "Provider updated",
                "Provider updated successfully",
                provider
        );
    }

    // ================= DEACTIVATE =================
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Response> deactivateProvider(@PathVariable Long id) {

        providerService.deactivateProvider(id);

        return ApiResponse.success(
                "Provider deactivated",
                "Provider deactivated successfully",
                null
        );
    }
}