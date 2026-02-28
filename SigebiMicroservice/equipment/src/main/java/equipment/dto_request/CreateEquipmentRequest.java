package equipment.dto_request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEquipmentRequest {

    @NotBlank(message = "Serie is required")
    @Size(max = 100, message = "Serie cannot exceed 100 characters")
    private String serie;

    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name cannot exceed 150 characters")
    private String name;

    @NotBlank(message = "Brand is required")
    @Size(max = 100, message = "Brand cannot exceed 100 characters")
    private String brand;

    @NotBlank(message = "Model is required")
    @Size(max = 100, message = "Model cannot exceed 100 characters")
    private String model;

    @NotNull(message = "Acquisition date is required")
    @PastOrPresent(message = "Acquisition date cannot be in the future")
    private Date acquisitionDate;

    @NotNull(message = "INVIMA code is required")
    @Size(max = 100, message = "The INVIMA code cannot exceed 100 characters")
    private String invima;

    @NotNull(message = "Classification is required")
    private Long classificationId;

    @NotNull(message = "Area is required")
    private Long areaId;

    @NotNull(message = "Provider is required")
    private Long providerId;

    @NotNull(message = "State is required")
    private Long stateId;

    @NotNull(message = "Location is required")
    private Long locationId;

    @NotNull(message = "Responsible user is required")
    private Long responsibleUserId;

    @Size(max = 50, message = "Risk level cannot exceed 50 characters")
    private String riskLevel;

    @Positive(message = "Useful life must be a positive number")
    private Integer usefulLife;

    private Date warrantyEnd;

    @Positive(message = "Maintenance frequency must be a positive number")
    private Integer maintenanceFrequency;

    @Positive(message = "Calibration frequency must be a positive number")
    private Integer calibrationFrequency;
}