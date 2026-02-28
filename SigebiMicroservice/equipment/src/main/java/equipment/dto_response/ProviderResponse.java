package equipment.dto_response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProviderResponse {

    private Long providerId;
    private String name;
    private String contactName;
    private String contactPhone;
    private String email;
    private String address;
    private Boolean active;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private Integer equipmentCount;
}