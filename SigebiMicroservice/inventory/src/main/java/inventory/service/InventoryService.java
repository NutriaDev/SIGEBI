package inventory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import inventory.client.LocationClient;
import inventory.client.UserClient;
import inventory.dto_response.ApiResponse;
import inventory.dto_response.LocationResponse;
import inventory.dto_response.UserResponse;
import inventory.entities.InventoryDetailEntity;
import inventory.kafka.InventoryEvent;
import inventory.kafka.InventoryEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import inventory.client.EquipmentClient;
import inventory.dto_response.EquipmentResponse;
import inventory.dto_request.InventoryRequest;
import inventory.entities.InventoryEntity;
import inventory.exception.BusinessException;
import inventory.exception.EquipmentNotFoundException;
import inventory.repository.InventoryRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final EquipmentClient equipmentClient;
    private final UserClient userClient;
    private final LocationClient locationClient;
    private final ObjectMapper objectMapper;
    private final InventoryEventProducer inventoryEventProducer;

    @Transactional
    public String createPhysicalInventory(InventoryRequest req) {
        Long userId = getCurrentUserId();
        String userName = resolveUserName(userId);
        String locationName = resolveLocationName(req.locationId());

        log.info("Creando inventario físico — locationId={}, userId={}, userName={}",
                req.locationId(), userId, userName);

        if (req.details() == null || req.details().isEmpty()) {
            throw new BusinessException("El inventario debe tener al menos un equipo");
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
                .locationName(locationName)
                .locationId(req.locationId())
                .date(req.date() != null ? req.date() : LocalDate.now())
                .observations(req.observations())
                .createdBy(String.valueOf(userId))
                .responsibleName(userName)
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

        int total = detailEntities.size();
        int active = (int) detailEntities.stream()
                .filter(d -> "ACTIVO".equalsIgnoreCase(d.getState()))
                .count();
        int inactive = total - active;

        InventoryEvent inventoryEvent = InventoryEvent.builder()
                .locationId(req.locationId())
                .locationName(locationName)
                .responsibleName(userName)
                .date(LocalDate.now())
                .totalEquipments(total)
                .activeEquipments(active)
                .inactiveEquipments(inactive)
                .build();

        inventoryEventProducer.send(inventoryEvent);

        log.info("Inventario creado correctamente — locationId={}", req.locationId());
        return "Inventario realizado correctamente";
    }

    private Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("Usuario no autenticado");
        }
        return (Long) authentication.getPrincipal();
    }

    private String resolveUserName(Long userId) {
        try {
            UserResponse user = userClient.getUserById(userId);
            if (user == null || user.getName() == null) {
                throw new BusinessException("No se pudo obtener información del usuario");
            }
            return user.getName();
        } catch (Exception e) {
            log.error("Error obteniendo nombre de usuario userId={}", userId, e);
            throw new BusinessException("Error al resolver datos del usuario");
        }
    }

    private String resolveLocationName(Long locationId) {
        try {
            ApiResponse response = locationClient.getLocationById(locationId);
            if (response == null || response.getBody() == null) {
                throw new BusinessException("No se pudo obtener información de la ubicación");
            }
            LocationResponse location = objectMapper.convertValue(response.getBody(), LocationResponse.class);
            return location.getName();
        } catch (Exception e) {
            log.error("Error obteniendo nombre de ubicación locationId={}", locationId, e);
            throw new BusinessException("Error al resolver datos de la ubicación");
        }
    }

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
        return objectMapper.convertValue(response.getBody(), EquipmentResponse.class);
    }
}