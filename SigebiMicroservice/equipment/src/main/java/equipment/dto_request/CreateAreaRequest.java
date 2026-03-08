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
public class CreateAreaRequest {

    @NotBlank(message = "El nombre del área es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe contener entre 2 y 100 caracteres")
    private String name;
}



