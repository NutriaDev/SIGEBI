package sigebi.reportsandaudit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EmptyResultException extends RuntimeException {

    private final String code;
    private final HttpStatus status;

    public EmptyResultException(String message) {
        super(message);
        this.code = "EMPTY_RESULT";
        this.status = HttpStatus.NO_CONTENT;
    }
}
