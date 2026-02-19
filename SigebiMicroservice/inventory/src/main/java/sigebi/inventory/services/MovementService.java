package sigebi.inventory.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sigebi.inventory.dto.MovementRequest;
import sigebi.inventory.entities.EquipmentEntity;
import sigebi.inventory.entities.LocationEntity;
import sigebi.inventory.entities.MovementEntity;
import sigebi.inventory.repositories.EquipmentRepository;
import sigebi.inventory.repositories.LocationRepository;
import sigebi.inventory.repositories.MovementRepository;

@Service
@RequiredArgsConstructor
public class MovementService {

    private final EquipmentRepository equipmentRepository;
    private final LocationRepository locationRepository;
    private final MovementRepository movementRepository;

    @Transactional
    public String registerMovement(MovementRequest req) {

        // 1) Validar equipo exista
        EquipmentEntity equipment = equipmentRepository.findById(req.equipmentId())
                .orElseThrow(() -> new IllegalArgumentException("El equipo no existe"));

        // 2) Validar ubicación destino exista
        LocationEntity destination = locationRepository.findById(req.destinationLocationId())
                .orElseThrow(() -> new IllegalArgumentException("La ubicación destino no existe"));

        // 3) Validar que el equipo NO esté bloqueado por mantenimiento
        // Ajusta el campo según tu EquipmentEntity real
        if (Boolean.TRUE.equals(equipment.getMaintenanceBlocked())) {
            throw new IllegalArgumentException("El equipo está bloqueado por mantenimiento");
        }

        // 4) Obtener origen (puede venir del request o del equipo)
        LocationEntity origin = locationRepository.findById(req.originLocationId())
                .orElseThrow(() -> new IllegalArgumentException("La ubicación origen no existe"));

        // (Opcional recomendado) validar que el equipo esté en origen
        if (equipment.getLocationEntity() == null ||
                !equipment.getLocationEntity().getIdLocation().equals(origin.getIdLocation())) {
            throw new IllegalArgumentException("El equipo no pertenece a la ubicación origen");
        }

        // 5) Registrar movimiento
        MovementEntity movement = MovementEntity.builder()
                .equipment(equipment)
                .originLocation(origin)
                .destinationLocation(destination)
                .reason(req.reason())
                .responsibleUserId(req.responsibleUserId())
                .build();

        movementRepository.save(movement);

        // 6) Actualizar ubicación y estado actual del equipo
        equipment.setLocationEntity(destination);

        // si tienes status:
        if (equipment.getStatus() != null) {
            equipment.setStatus("AVAILABLE"); // ajusta a tu regla
        }

        equipmentRepository.save(equipment);

        return "Movimiento registrado correctamente";
    }
}
