package inventory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import inventory.dto_request.UpdateEquipmentLocationRequest;
import inventory.dto_response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import inventory.client.EquipmentClient;
import inventory.dto_response.EquipmentResponse;
import inventory.dto_request.UpdateLocationRequest;
import inventory.dto_request.MovementRequest;
import inventory.entities.MovementEntity;
import inventory.exception.BusinessException;
import inventory.exception.EquipmentNotFoundException;
import inventory.repository.MovementRepository;
import inventory.util.RoleValidator;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovementService {

    private final MovementRepository movementRepository;
    private final EquipmentClient equipmentClient;
    private final ObjectMapper objectMapper;

    private EquipmentResponse validateEquipment(String serie) {

        ApiResponse response;

        try {
            response = equipmentClient.findBySerial(serie);
        } catch (Exception e) {
            log.error("Error consultando equipo {} en Equipment MS",
                    serie, e);
            throw new EquipmentNotFoundException("El equipo no existe");
        }

        if (response == null || response.getBody() == null) {
            throw new EquipmentNotFoundException("El equipo no existe");
        }

        EquipmentResponse equipment = objectMapper.convertValue(
                response.getBody(),
                EquipmentResponse.class
        );


        if (Boolean.TRUE.equals(equipment.getMaintenanceBlocked()) ||
                "MAINTENANCE".equalsIgnoreCase(equipment.getStatus())) {
            throw new BusinessException(
                    "El equipo está bloqueado por mantenimiento");
        }

        return equipment;
    }

    @Transactional
    public void registerMovement(MovementRequest req) {

        RoleValidator.validate(req.userRole());

        EquipmentResponse equipment = validateEquipment(req.serie());

        Long equipmentId = equipment.getId();

        if (!req.originLocationId().equals(equipment.getLocationId())) {
            throw new BusinessException(
                    "El equipo no pertenece a la ubicación origen");
        }

        MovementEntity movement = MovementEntity.builder()
                .equipmentId(equipmentId)
                .originLocationId(req.originLocationId())
                .destinationLocationId(req.destinationLocationId())
                .reason(req.reason())
                .responsibleUserId(req.responsibleUserId())
                .build();

        movementRepository.save(movement);

        try {
            equipmentClient.updateLocation(
                    equipmentId,
                    new UpdateEquipmentLocationRequest(req.destinationLocationId())
            );
        } catch (Exception e) {
            log.error("ERROR REAL:", e);
            throw new BusinessException(
                    "No se pudo actualizar la ubicación del equipo");
        }
    }
}