package dto_request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateEquipmentRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String type;
}