package inventory.kafka;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovementEvent {
    private Long equipmentId;
    private Long originLocationId;
    private Long destinationLocationId;
    private LocalDate date;
    private String responsibleUserName;
}
