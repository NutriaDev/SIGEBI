package sigebi.inventory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import sigebi.inventory.dto_response.MovementResponse;
import sigebi.inventory.dto_response.PagedResponse;
import sigebi.inventory.entities.MovementEntity;
import sigebi.inventory.repository.MovementRepository;
import sigebi.inventory.specifications.MovementSpecifications;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovementQueryService {

    private final MovementRepository movementRepository;

    public PagedResponse<MovementResponse> getMovements(
            int page, int limit, Long equipmentId, Long locationId, LocalDate date) {

        log.info("Consultando movimientos — page={}, limit={}, equipmentId={}, locationId={}, date={}",
                page, limit, equipmentId, locationId, date);

        if (page < 0) page = 0;
        if (limit <= 0) limit = 10;
        if (limit > 100) limit = 100;

        Pageable pageable = PageRequest.of(
                page, limit, Sort.by(Sort.Direction.DESC, "idMovement"));

        Specification<MovementEntity> spec = Specification
                .where(MovementSpecifications.byEquipmentId(equipmentId))
                .and(MovementSpecifications.byOriginLocationId(locationId))
                .and(MovementSpecifications.byDate(date));

        Page<MovementEntity> result = movementRepository.findAll(spec, pageable);

        List<MovementResponse> content = result.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return new PagedResponse<>(
                content,
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    private MovementResponse mapToResponse(MovementEntity m) {
        return new MovementResponse(
                m.getIdMovement(),
                m.getEquipmentId(),
                m.getOriginLocationId(),
                m.getDestinationLocationId(),
                m.getDate(),
                m.getReason(),
                m.getResponsibleUserId(),
                m.getCreatedAt()
        );
    }
}
