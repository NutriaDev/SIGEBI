package sigebi.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sigebi.auth.DTO.request.LoginRequest;
import sigebi.auth.DTO.response.LoginResponse;
import sigebi.auth.DTO.response.Response;
import sigebi.auth.constants.ErrorTitles;
import sigebi.auth.service.LoginService;
import sigebi.auth.utils.ApiResponse;

@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;

    @PostMapping(value = "/login")
    public ResponseEntity<Response> login(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        try {

            LoginResponse loginResponse = loginService.login(loginRequest);

            return ApiResponse.success(
                    "user login",
                    "user login successful",
                    loginResponse
            );

        } catch (Exception e) {
            log.error("Something went wrong with login", e);
            return ApiResponse.internalError(
                    ErrorTitles.LOGIN_FAILED,
                    e.getMessage()
            );
        }
    }

    @GetMapping("/secure-test")
    public ResponseEntity<String> secureTest() {
        var auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        return ResponseEntity.ok(
                "UserId=" + auth.getPrincipal()
                        + " | Roles=" + auth.getAuthorities()
        );
    }

}
