package inventory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import inventory.dto_request.UpdateEquipmentLocationRequest;
import inventory.dto_response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import inventory.client.EquipmentClient;
import inventory.dto_response.EquipmentResponse;
import inventory.dto_request.UpdateEquipmentLocationRequest;
import inventory.dto_request.MovementRequest;
import inventory.entities.MovementEntity;
import inventory.exception.BusinessException;
import inventory.exception.EquipmentNotFoundException;
import inventory.kafka.ReportEvent;
import inventory.kafka.ReportEventProducer;
import inventory.repository.MovementRepository;
import inventory.util.RoleValidator;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovementService {

    private final MovementRepository movementRepository;
    private final EquipmentClient equipmentClient;
    private final ObjectMapper objectMapper;
    private final ReportEventProducer reportEventProducer;

    @SuppressWarnings("unchecked")
    private EquipmentResponse validateEquipment(String serial) {

        ApiResponse response;

        try {
            response = equipmentClient.findBySerial(serial);
            log.info("Llamando equipment con serie: {}", serial);
        } catch (Exception e) {
            log.error("Error consultando equipo {}", serial, e);
            throw new EquipmentNotFoundException("Equipo no encontrado");
        }

        if (response == null || response.getBody() == null) {
            throw new EquipmentNotFoundException("Equipo no encontrado");
        }

        // 🔥 AQUÍ ESTÁ EL FIX REAL
        EquipmentResponse equipment = objectMapper.convertValue(
                response.getBody(),
                EquipmentResponse.class
        );

        return equipment;
    }

    @Transactional
    public void registerMovement(MovementRequest req) {

        // 🔥 1. BUSCAR EQUIPO POR SERIAL
        EquipmentResponse equipment = validateEquipment(req.serial());

        Long equipmentId = equipment.getEquipmentId();
        Long currentLocation = equipment.getLocationId();

        // 🔥 2. VALIDAR UBICACIÓN ORIGEN
        if (!req.originLocationId().equals(currentLocation)) {
            throw new BusinessException("El equipo no pertenece a la ubicación origen");
        }

        Long userId = getCurrentUserId();

        // 🔥 3. GUARDAR MOVIMIENTO
        MovementEntity movement = MovementEntity.builder()
                .equipmentId(equipmentId)
                .originLocationId(req.originLocationId())
                .destinationLocationId(req.destinationLocationId())
                .reason(req.reason())
                .responsibleUserId(userId)
                .build();

        movementRepository.save(movement);

        ReportEvent reportEvent = ReportEvent.builder()
                .eventType("MOVEMENT")
                .equipmentId(equipmentId)
                .equipmentName("Equipo-" + equipmentId)
                .location("Destino-" + req.destinationLocationId())
                .status("COMPLETED")
                .date(LocalDate.now())
                .build();
        reportEventProducer.send(reportEvent);

        // 🔥 4. ACTUALIZAR UBICACIÓN EN EQUIPMENT
        try {
            equipmentClient.updateLocation(
                    equipmentId,
                    new UpdateEquipmentLocationRequest(req.destinationLocationId())
            );
        } catch (Exception e) {
            log.error("Error actualizando ubicación", e);
            throw new BusinessException("No se pudo actualizar la ubicación del equipo");
        }
    }

    @Transactional(readOnly = true)
    public EquipmentResponse findEquipmentBySerial(String serial) {
        return validateEquipment(serial);
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

}