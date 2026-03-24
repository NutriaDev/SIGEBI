package sigebi.users.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sigebi.users.dto_request.InternalAuthValidateRequest;
import sigebi.users.dto_request.UpdatePasswordInternalDto;
import sigebi.users.dto_response.UserAuthDataResponse;
import sigebi.users.dto_response.UserBasicResponse;
import sigebi.users.service.InternalAuthService;

@RestController
@RequestMapping("/internal/auth")
@RequiredArgsConstructor
public class InternalAuthController {

    private final InternalAuthService internalAuthService;

    // 🔐 Validar credenciales
    @PostMapping("/validate")
    public ResponseEntity<UserAuthDataResponse> validate(
            @RequestBody InternalAuthValidateRequest request) {

        return ResponseEntity.ok(
                internalAuthService.validateCredentials(
                        request.getEmail(),
                        request.getPassword()
                )
        );
    }

    // 👤 Obtener usuario por ID
    @GetMapping("/users/{id}")
    public ResponseEntity<UserAuthDataResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(internalAuthService.getUserById(id));
    }

    // 📧 Obtener usuario básico por email
    @GetMapping("/users/by-email/{email}")
    public ResponseEntity<UserBasicResponse> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(internalAuthService.findBasicByEmail(email));
    }

    // 🔑 Actualizar contraseña
    @PatchMapping("/users/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable Long id,
            @RequestBody UpdatePasswordInternalDto req) {

        internalAuthService.updateHashedPassword(id, req.hashedPassword());
        return ResponseEntity.noContent().build();
    }
}