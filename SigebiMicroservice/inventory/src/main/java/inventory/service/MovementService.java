package inventory.service;

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

    private EquipmentResponse validateEquipment(Long equipmentId) {

        ApiResponse response;

        try {
            response = equipmentClient.getEquipmentById(equipmentId);
        } catch (Exception e) {
            log.error("Error consultando equipo {} en Equipment MS",
                    equipmentId, e);
            throw new EquipmentNotFoundException("El equipo no existe");
        }

        if (response == null || response.getBody() == null) {
            throw new EquipmentNotFoundException("El equipo no existe");
        }

        EquipmentResponse equipment = (EquipmentResponse) response.getBody();


        if (Boolean.TRUE.equals(equipment.getMaintenanceBlocked()) ||
                "MAINTENANCE".equalsIgnoreCase(equipment.getStatus())) {
            throw new BusinessException(
                    "El equipo está bloqueado por mantenimiento");
        }

        return equipment;
    }

    @Transactional
    public void registerMovement(MovementRequest req) {
        log.info("Registrando movimiento — equipmentId={}, origin={}, destination={}, user={}",
                req.equipmentId(),
                req.originLocationId(),
                req.destinationLocationId(),
                req.responsibleUserId());

        RoleValidator.validate(req.userRole());

        EquipmentResponse equipment = validateEquipment(req.equipmentId());

        if (!req.originLocationId().equals(equipment.getLocationId())) {
            throw new BusinessException(
                    "El equipo no pertenece a la ubicación origen");
        }

        MovementEntity movement = MovementEntity.builder()
                .equipmentId(req.equipmentId())
                .originLocationId(req.originLocationId())
                .destinationLocationId(req.destinationLocationId())
                .reason(req.reason())
                .responsibleUserId(req.responsibleUserId())
                .build();

        movementRepository.save(movement);

        try {
            equipmentClient.updateLocation(
                    req.equipmentId(),
                    new UpdateLocationRequest(req.destinationLocationId())
            );
            log.info("Ubicación del equipo {} actualizada a {}",
                    req.equipmentId(), req.destinationLocationId());
        } catch (Exception e) {
            log.error("Error actualizando ubicación del equipo {}: {}",
                    req.equipmentId(), e.getMessage());
            throw new BusinessException(
                    "No se pudo actualizar la ubicación del equipo");
        }

        log.info("Movimiento registrado correctamente para equipo {}",
                req.equipmentId());
    }
}