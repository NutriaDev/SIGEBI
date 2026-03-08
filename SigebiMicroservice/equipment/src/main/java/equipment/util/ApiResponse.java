package equipment.util;

import equipment.dto_response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponse {
    private ApiResponse() {} // Prevent instantiation

    // Successful response
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

    // Resource created (POST)
    public static ResponseEntity<Response> created(String title, String message, Object body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.builder()
                        .status("success")
                        .title(title)
                        .message(message)
                        .body(body)
                        .build());
    }

    // Resource not found
    public static ResponseEntity<Response> notFound(String title, String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Response.builder()
                        .status("error")
                        .title(title)
                        .message(message)
                        .build());
    }

    // Internal server error
    public static ResponseEntity<Response> internalError(String title, String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.builder()
                        .status("error")
                        .title(title)
                        .message(message)
                        .build());
    }
}
