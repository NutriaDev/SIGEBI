package sigebi.inventory.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sigebi.inventory.dto.InventoryRequest;
import sigebi.inventory.entities.*;
import sigebi.inventory.repositories.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final LocationRepository locationRepository;
    private final EquipmentRepository equipmentRepository;

    @Transactional
    public String createPhysicalInventory(InventoryRequest req) {

        // 1) Validar ubicación exista
        LocationEntity location = locationRepository.findById(req.locationId())
                .orElseThrow(() -> new IllegalArgumentException("La ubicación no existe"));

        // 2) Validar equipos recibidos existan
        List<Long> physicalIds = req.details().stream()
                .map(d -> d.equipmentId())
                .toList();

        Map<Long, EquipmentEntity> equipmentsById = equipmentRepository.findAllById(physicalIds)
                .stream().collect(Collectors.toMap(EquipmentEntity::getIdEquipment, e -> e));

        if (equipmentsById.size() != physicalIds.size()) {
            // detectar cuáles no existen
            Set<Long> found = equipmentsById.keySet();
            List<Long> missing = physicalIds.stream().filter(id -> !found.contains(id)).toList();
            throw new IllegalArgumentException("Equipos no registrados: " + missing);
        }

        // 3) Crear inventory (cabecera)
        InventoryEntity inv = InventoryEntity.builder()
                .location(location.getName()) // campo texto “location”
                .locationEntity(location)     // FK real
                .date(LocalDate.now())
                .observations(req.observations())
                .createdBy(req.createdBy())
                .active(true)
                .build();

        // 4) Crear detalles físicos
        List<InventoryDetailEntity> detailEntities = new ArrayList<>();
        for (var d : req.details()) {
            EquipmentEntity eq = equipmentsById.get(d.equipmentId());

            InventoryDetailEntity det = InventoryDetailEntity.builder()
                    .inventory(inv)
                    .equipment(eq)
                    .state(d.state())
                    .observations(d.observations())
                    .build();

            detailEntities.add(det);
        }
        inv.setDetails(detailEntities); // tu InventoryEntity debe tener: List<InventoryDetailEntity> details

        // 5) Inconsistencias (registrados vs físicos)
        // registrados = equipos en esa ubicación
        List<EquipmentEntity> registered = equipmentRepository.findByLocationEntity_IdLocation(req.locationId());
        Set<Long> registeredIds = registered.stream().map(EquipmentEntity::getIdEquipment).collect(Collectors.toSet());
        Set<Long> physicalSet = new HashSet<>(physicalIds);

        Set<Long> missingInPhysical = new HashSet<>(registeredIds);
        missingInPhysical.removeAll(physicalSet); // estaban en sistema, no aparecieron físicamente

        Set<Long> extraInPhysical = new HashSet<>(physicalSet);
        extraInPhysical.removeAll(registeredIds); // aparecen físicamente, pero no estaban registrados en esa ubicación

        // Guarda la evidencia en observations (simple y cumple “marcar inconsistencias”)
        String inconsistencies = "\nINCONSISTENCIAS:\n" +
                "Faltan físicamente (registrados en sistema): " + missingInPhysical + "\n" +
                "Sobrantes físicamente (no registrados en ubicación): " + extraInPhysical;

        inv.setObservations((inv.getObservations() == null ? "" : inv.getObservations()) + inconsistencies);

        inventoryRepository.save(inv);

        return "Inventario realizado correctamente";
    }
}
