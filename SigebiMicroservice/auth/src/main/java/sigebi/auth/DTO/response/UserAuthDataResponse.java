package sigebi.auth.DTO.response;

import java.util.List;

public record UserAuthDataResponse(
        Long userId,
        List<String> roles

) {}

