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
public class AreaResponse {

    private Long areaId;
    private String name;
    private Boolean active;
    private Date createdAt;
    private Date updatedAt;
    private Integer equipmentCount;
}
