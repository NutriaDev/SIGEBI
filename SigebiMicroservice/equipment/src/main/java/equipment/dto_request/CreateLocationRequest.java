package equipment.dto_request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLocationRequest {

    @NotBlank(message = "El nombre de la ubicación es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;

    @Size(max = 50, message = "El tipo no puede exceder 50 caracteres")
    private String type;

    @Size(max = 50, message = "El piso no puede exceder 50 caracteres")
    private String floor;

    @Size(max = 255, message = "El detalle no puede exceder 255 caracteres")
    private String detail;
}