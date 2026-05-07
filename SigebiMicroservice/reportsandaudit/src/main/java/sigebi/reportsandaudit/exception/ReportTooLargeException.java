package sigebi.reportsandaudit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ReportTooLargeException extends RuntimeException {

    private final String code;
    private final HttpStatus status;

    public ReportTooLargeException(String message) {
        super(message);
        this.code = "REPORT_TOO_LARGE";
        this.status = HttpStatus.UNPROCESSABLE_ENTITY;
    }
}
