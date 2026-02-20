package sigebi.users.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import sigebi.users.constants.ErrorTitles;
import sigebi.users.dto_response.Response;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Response buildError(String title, String message) {
        return Response.builder()
                .status("error")
                .title(title)
                .message(message)
                .build();
    }

    // 🔹 ResponseStatusException (401, 403, etc)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Response> handleResponseStatus(ResponseStatusException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(buildError(
                        ex.getStatusCode().toString(),
                        ex.getReason()
                ));
    }

    // 🔹 Email duplicado → 409
    @ExceptionHandler(EmailException.class)
    public ResponseEntity<Response> handleEmail(EmailException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError("EMAIL_ERROR", ex.getMessage()));
    }

    // 🔹 Validaciones generales → 400
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
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(ErrorTitles.INTERNAL_ERROR, ex.getMessage()));
    }
}

