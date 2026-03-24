package sigebi.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import sigebi.auth.DTO.request.InternalAuthValidateRequest;
import sigebi.auth.DTO.response.UserAuthDataResponse;
import sigebi.auth.DTO.response.UserBasicResponse;

@FeignClient(
        name = "ms-users",
        url = "http://localhost:8090"
)
public interface UserInternalClient {
    @PostMapping("/internal/auth/validate")
    UserAuthDataResponse validate(
            @RequestBody InternalAuthValidateRequest request
    );

    @GetMapping("/internal/users/{id}")
    UserAuthDataResponse getById(@PathVariable Long id);

    // ── NUEVOS para reset password ──────────────────────────────────────────
    @GetMapping("/internal/users/by-email/{email}")
    UserBasicResponse getUserByEmail(@PathVariable("email") String email);

    @PatchMapping("/internal/users/{id}/password")
    void updatePassword(@PathVariable("id") Long userId,
                        @RequestBody String request);

}
