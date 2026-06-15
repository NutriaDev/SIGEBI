package sigebi.reportsandaudit.dto_request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SparePartItem {
    private Integer quantity;
    private String reference;
    private String description;
}
