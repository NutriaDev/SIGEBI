package sigebi.auth.DTO.response;

import java.util.List;

public record UserAuthDataResponse(
        Long userId,
        String email,
        String name,
        List<String> roles

) {}

