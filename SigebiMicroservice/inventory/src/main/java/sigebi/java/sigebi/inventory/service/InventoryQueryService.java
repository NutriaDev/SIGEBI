package sigebi.inventory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import sigebi.inventory.dto_response.*;
import sigebi.inventory.entities.InventoryEntity;
import sigebi.inventory.exception.BusinessException;
import sigebi.inventory.repository.InventoryRepository;
import sigebi.inventory.specifications.InventorySpecifications;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryQueryService {

    private final InventoryRepository inventoryRepository;

    public PagedResponse<InventoryResponse> getInventories(
            int page, int limit, Long locationId, LocalDate date) {

        log.info("Consultando inventarios — page={}, limit={}, locationId={}, date={}",
                page, limit, locationId, date);

        if (page < 0) page = 0;
        if (limit <= 0) limit = 10;
        if (limit > 100) limit = 100;

        Pageable pageable = PageRequest.of(
                page, limit, Sort.by(Sort.Direction.DESC, "idInventory"));

        Specification<InventoryEntity> spec = Specification
                .where(InventorySpecifications.byLocationId(locationId))
                .and(InventorySpecifications.byDate(date));

        Page<InventoryEntity> result = inventoryRepository.findAll(spec, pageable);

        List<InventoryResponse> content = result.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return new PagedResponse<>(
                content,
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    public InventoryWithDetailResponse getInventoryById(Long id) {
        log.info("Consultando detalle de inventario id={}", id);

        InventoryEntity inv = inventoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Inventario no encontrado con id: " + id));

        List<InventoryDetailResponse> details = inv.getDetails().stream()
                .map(d -> new InventoryDetailResponse(
                        d.getIdDetail(),
                        d.getEquipmentId(),
                        d.getState(),
                        d.getObservations()
                ))
                .toList();

        return new InventoryWithDetailResponse(
                inv.getIdInventory(),
                inv.getLocation(),
                inv.getDate(),
                inv.getObservations(),
                inv.getCreatedAt(),
                details
        );
    }

    private InventoryResponse mapToResponse(InventoryEntity inv) {
        return new InventoryResponse(
                inv.getIdInventory(),
                inv.getLocation(),
                inv.getDate(),
                inv.getObservations(),
                inv.getCreatedAt()
        );
    }
}
