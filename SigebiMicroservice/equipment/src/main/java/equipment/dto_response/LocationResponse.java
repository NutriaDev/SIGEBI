package equipment.dto_response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationResponse {

    private Long locationId;
    private String name;
    private String type;
    private String floor;
    private String detail;
    private Boolean active;
    private Date createdAt;
    private Date updatedAt;
    private Integer equipmentCount;
    private Integer inventoryCount;
}