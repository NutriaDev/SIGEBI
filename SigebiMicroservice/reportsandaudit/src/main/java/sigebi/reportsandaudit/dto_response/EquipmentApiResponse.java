package sigebi.reportsandaudit.dto_response;

import lombok.*;
import sigebi.reportsandaudit.client.EquipmentDetail;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentApiResponse {
    private String status;
    private String title;
    private String message;
    private EquipmentDetail body;
}
