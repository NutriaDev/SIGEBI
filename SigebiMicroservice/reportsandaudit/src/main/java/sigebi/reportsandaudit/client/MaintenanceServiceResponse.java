package sigebi.reportsandaudit.client;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceServiceResponse {

    private String status;
    private String title;
    private String message;
    private MaintenanceDetail body;
}
