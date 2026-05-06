package sigebi.maintenance.dto_response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentApiResponse {
    private String status;
    private String title;
    private String message;
    private Object body;
}
