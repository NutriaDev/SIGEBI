package sigebi.reportsandaudit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PermissionDeniedException extends RuntimeException {

    private final String code;
    private final HttpStatus status;

    public PermissionDeniedException(String message) {
        super(message);
        this.code = "PERMISSION_DENIED";
        this.status = HttpStatus.FORBIDDEN;
    }
}
