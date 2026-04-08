package sigebi.inventory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sigebi.inventory.client.EquipmentClient;
import sigebi.inventory.dto_response.EquipmentResponse;
import sigebi.inventory.dto_request.InventoryRequest;
import sigebi.inventory.entities.InventoryDetailEntity;
import sigebi.inventory.entities.InventoryEntity;
import sigebi.inventory.exception.BusinessException;
import sigebi.inventory.exception.EquipmentNotFoundException;
import sigebi.inventory.repository.InventoryRepository;
import sigebi.inventory.util.RoleValidator;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final EquipmentClient equipmentClient;

    @Transactional
    public String createPhysicalInventory(InventoryRequest req) {
        log.info("Creando inventario físico — locationId={}, createdBy={}",
                req.locationId(), req.createdBy());

        RoleValidator.validate(req.userRole());

        if (req.details() == null || req.details().isEmpty()) {
            throw new BusinessException(
                    "El inventario debe tener al menos un equipo");
        }

        List<Long> physicalIds = req.details().stream()
                .map(d -> d.equipmentId())
                .toList();

        Map<Long, EquipmentResponse> equipmentsById = physicalIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> {
                            try {
                                return equipmentClient.getEquipmentById(id);
                            } catch (Exception e) {
                                log.error("Equipo {} no encontrado en Equipment MS", id);
                                throw new EquipmentNotFoundException(
                                        "El equipo " + id + " no existe");
                            }
                        }
                ));

        InventoryEntity inv = InventoryEntity.builder()
                .location(req.location())
                .locationId(req.locationId())
                .date(req.date() != null ? req.date() : LocalDate.now())
                .observations(req.observations())
                .createdBy(req.createdBy())
                .active(true)
                .build();

        List<InventoryDetailEntity> detailEntities = new ArrayList<>();
        for (var d : req.details()) {
            detailEntities.add(InventoryDetailEntity.builder()
                    .inventory(inv)
                    .equipmentId(d.equipmentId())
                    .state(d.state())
                    .observations(d.observations())
                    .build());
        }
        inv.setDetails(detailEntities);

        Set<Long> registeredIds = equipmentsById.values().stream()
                .filter(eq -> req.locationId().equals(eq.getLocationId()))
                .map(EquipmentResponse::getId)
                .collect(Collectors.toSet());

        Set<Long> physicalSet = new HashSet<>(physicalIds);

        Set<Long> missingInPhysical = new HashSet<>(registeredIds);
        missingInPhysical.removeAll(physicalSet);

        Set<Long> extraInPhysical = new HashSet<>(physicalSet);
        extraInPhysical.removeAll(registeredIds);

        String inconsistencies = "\nINCONSISTENCIAS:" +
                "\nFaltantes físicamente: " + missingInPhysical +
                "\nSobrantes físicamente: " + extraInPhysical;

        inv.setObservations(
                (inv.getObservations() == null ? "" : inv.getObservations())
                        + inconsistencies);

        inventoryRepository.save(inv);

        log.info("Inventario creado correctamente — locationId={}", req.locationId());
        return "Inventario realizado correctamente";
    }
}