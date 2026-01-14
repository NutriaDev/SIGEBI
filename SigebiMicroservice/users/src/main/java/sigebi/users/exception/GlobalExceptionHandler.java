package sigebi.users.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sigebi.users.constants.ErrorTitles;
import sigebi.users.dto_response.Response;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // ===========================
    // 🔹 Método reutilizable
    // ===========================
    private Response buildError(String title, String message) {
        return Response.builder()
                .status("error")
                .title(title)
                .message(message)
                .build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Response> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(ErrorTitles.USER_NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<Response> handleRoleNotFound(RoleNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(ErrorTitles.ROLE_NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<Response> handleEmail(EmailException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(ErrorTitles.ROLE_NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Response> handleEmail(BusinessException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(ErrorTitles.ROLE_NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(ErrorTitles.INTERNAL_ERROR, ex.getMessage()));
    }

}
