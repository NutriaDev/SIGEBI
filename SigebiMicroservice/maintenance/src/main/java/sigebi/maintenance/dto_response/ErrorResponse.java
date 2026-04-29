package sigebi.maintenance.dto_response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {

    private String code;
    private String message;
    private LocalDateTime timestamp;
}
