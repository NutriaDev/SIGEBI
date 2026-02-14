package sigebi.users.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sigebi.users.dto_request.InternalAuthValidateRequest;
import sigebi.users.dto_response.UserAuthDataResponse;
import sigebi.users.service.InternalAuthService;

@RestController
@RequestMapping("/internal/auth")
@RequiredArgsConstructor
public class InternalAuthController {

    private final InternalAuthService internalAuthService;

    @PostMapping("/validate")
    public UserAuthDataResponse validate(@RequestBody InternalAuthValidateRequest request) {
        return internalAuthService.validateCredentials(
                request.getEmail(),
                request.getPassword()
        );
    }

}

