package sigebi.users.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import sigebi.users.constants.ErrorTitles;
import sigebi.users.dto_response.Response;

import org.springframework.security.access.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private Response buildError(String title, String message) {
        return Response.builder()
                .status("error")
                .title(title)
                .message(message)
                .build();
    }

    // 🔹 ResponseStatusException
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Response> handleResponseStatus(ResponseStatusException ex) {

        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());

        return ResponseEntity
                .status(status)
                .body(buildError(
                        status.getReasonPhrase(),
                        ex.getReason()
                ));
    }
    // 🔹 Acceso denegado → 403
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(buildError(
                        "FORBIDDEN",
                        "No está autorizado para realizar esta acción."
                ));
    }

    // 🔹 Email duplicado → 409
    @ExceptionHandler(EmailException.class)
    public ResponseEntity<Response> handleEmail(EmailException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError("EMAIL_ERROR", ex.getMessage()));
    }

    // 🔹 Validaciones → 400
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Response> handleBusiness(BusinessException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError("VALIDATION_ERROR", ex.getMessage()));
    }

    // 🔹 Not found → 404
    @ExceptionHandler({
            UserNotFoundException.class,
            RoleNotFoundException.class,
            CompanyNotFoundException.class
    })
    public ResponseEntity<Response> handleNotFound(RuntimeException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError("NOT_FOUND", ex.getMessage()));
    }
    // 🔹 Catch-all → 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleGeneral(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(ErrorTitles.INTERNAL_ERROR, ex.getMessage()));
    }
}

