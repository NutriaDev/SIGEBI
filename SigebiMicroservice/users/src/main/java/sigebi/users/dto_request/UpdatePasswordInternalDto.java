package sigebi.users.dto_request;

import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordInternalDto(
        @NotBlank String hashedPassword
) {}
