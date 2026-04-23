package sigebi.maintenance.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import sigebi.maintenance.dto_response.ErrorResponse;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 🔴 Business errors
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .code(ex.getCode())
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // 🔴 Validaciones DTO (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(e -> e.getDefaultMessage())
                .orElse("Error de validación");

        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .code("VALIDATION_ERROR")
                        .message(message)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // 🔴 Errores inesperados
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse.builder()
                        .code("INTERNAL_ERROR")
                        .message("Error inesperado")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}