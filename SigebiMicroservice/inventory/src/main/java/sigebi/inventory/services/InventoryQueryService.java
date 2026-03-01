package sigebi.inventory.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import sigebi.inventory.dto.response.InventoryResponse;
import sigebi.inventory.dto.response.PagedResponse;
import sigebi.inventory.entities.InventoryEntity;
import sigebi.inventory.repositories.InventoryRepository;
import sigebi.inventory.specifications.InventorySpecifications;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryQueryService {

    private final InventoryRepository inventoryRepository;

    public PagedResponse<InventoryResponse> getInventories(int page, int limit, Long locationId, LocalDate date) {

        // seguridad básica para evitar valores raros
        if (page < 0) page = 0;
        if (limit <= 0) limit = 10;
        if (limit > 100) limit = 100;

        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "idInventory"));

        Specification<InventoryEntity> spec = Specification
                .where(InventorySpecifications.byLocationId(locationId))
                .and(InventorySpecifications.byDate(date));

        Page<InventoryEntity> result = inventoryRepository.findAll(spec, pageable);

        List<InventoryResponse> content = result.getContent().stream()
                .map(inv -> new InventoryResponse(
                        inv.getIdInventory(),
                        inv.getLocationEntity() != null ? inv.getLocationEntity().getName() : inv.getLocation(),
                        inv.getDate(),
                        inv.getObservations(),
                        inv.getCreatedAt()
                ))
                .toList();

        return new PagedResponse<>(
                content,
                result.getTotalElements(),
                result.getTotalPages()
        );
    }
}
