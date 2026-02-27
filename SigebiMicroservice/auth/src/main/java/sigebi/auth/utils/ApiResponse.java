package sigebi.auth.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import sigebi.auth.DTO.response.Response;

public class ApiResponse {
    private ApiResponse() {}

    // ✅ Respuesta exitosa
    public static ResponseEntity<Response> success(String title, String message, Object body) {
        return ResponseEntity.ok(
                Response.builder()
                        .status("success")
                        .title(title)
                        .message(message)
                        .body(body)
                        .build()
        );
    }

    // ✅ Recurso creado (POST)
    public static ResponseEntity<Response> created(String title, String message, Object body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.builder()
                        .status("success")
                        .title(title)
                        .message(message)
                        .body(body)
                        .build());
    }

    // ⚠️ Recurso no encontrado
    public static ResponseEntity<Response> notFound(String title, String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Response.builder()
                        .status("error")
                        .title(title)
                        .message(message)
                        .build());
    }

    // ❌ Error interno
    public static ResponseEntity<Response> internalError(String title, String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.builder()
                        .status("error")
                        .title(title)
                        .message(message)
                        .build());
    }
}
