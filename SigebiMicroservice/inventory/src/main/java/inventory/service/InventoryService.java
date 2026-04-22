package inventory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import inventory.dto_response.ApiResponse;
import inventory.entities.InventoryDetailEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import inventory.client.EquipmentClient;
import inventory.dto_response.EquipmentResponse;
import inventory.dto_request.InventoryRequest;
import inventory.entities.InventoryEntity;
import inventory.exception.BusinessException;
import inventory.exception.EquipmentNotFoundException;
import inventory.repository.InventoryRepository;
import inventory.util.RoleValidator;


import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final EquipmentClient equipmentClient;
    private final ObjectMapper objectMapper;

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
                        this::validateEquipment
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
                .map(EquipmentResponse::getEquipmentId)
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

    // 🔥 MÉTODO CLAVE
    private EquipmentResponse validateEquipment(Long equipmentId) {

        ApiResponse response;

        try {
            response = equipmentClient.getEquipmentById(equipmentId);
        } catch (Exception e) {
            log.error("ERROR REAL:", e);
            throw new EquipmentNotFoundException("El equipo no existe");
        }

        if (response == null || response.getBody() == null) {
            throw new EquipmentNotFoundException("El equipo no existe");
        }


        EquipmentResponse equipment = objectMapper.convertValue(
                response.getBody(),
                EquipmentResponse.class
        );

        return equipment;
    }
}