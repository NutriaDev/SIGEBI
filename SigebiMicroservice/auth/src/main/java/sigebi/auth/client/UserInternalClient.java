package sigebi.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import sigebi.auth.DTO.request.InternalAuthValidateRequest;
import sigebi.auth.DTO.request.UpdatePasswordFeignRequest;
import sigebi.auth.DTO.response.UserAuthDataResponse;
import sigebi.auth.DTO.response.UserBasicResponse;

@FeignClient(name = "ms-users", url = "http://localhost:8090")
public interface UserInternalClient {

    @PostMapping("/internal/auth/validate")
    UserAuthDataResponse validate(@RequestBody InternalAuthValidateRequest request);

    @GetMapping("/internal/auth/users/{id}")
    UserAuthDataResponse getById(@PathVariable Long id);

    // ── Para reset password ─────────────────────────────────────────────────
    @GetMapping("/internal/auth/users/by-email/{email}")
    UserBasicResponse getUserByEmail(@PathVariable("email") String email);

    @PatchMapping("/internal/auth/users/{id}/password")
    void updatePassword(@PathVariable("id") Long userId,
                        @RequestBody UpdatePasswordFeignRequest request);
}