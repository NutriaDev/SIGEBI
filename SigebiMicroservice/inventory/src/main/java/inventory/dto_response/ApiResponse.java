package inventory.dto_response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private String status;
    private String title;
    private String message;
    private Object body;
}
