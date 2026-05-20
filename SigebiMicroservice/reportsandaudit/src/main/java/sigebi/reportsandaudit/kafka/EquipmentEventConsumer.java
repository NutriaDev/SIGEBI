package sigebi.reportsandaudit.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import sigebi.reportsandaudit.entities.EquipmentHistorialEntity;
import sigebi.reportsandaudit.entities.EquipmentSnapshotEntity;
import sigebi.reportsandaudit.repository.EquipmentHistorialRepository;
import sigebi.reportsandaudit.repository.EquipmentSnapshotRepository;



@Slf4j
@Service
@RequiredArgsConstructor
public class EquipmentEventConsumer {

    private final EquipmentSnapshotRepository snapshotRepository;
    private final EquipmentHistorialRepository historialRepository;

    @KafkaListener(
            topics = "${kafka.topics.equipment-events}",
            groupId = "sigebi-report-group"
    )
    public void consume(EquipmentEvent event) {
        try {
            if ("DELETED".equals(event.getEventType())) {
                snapshotRepository.findByEquipmentId(event.getEquipmentId())
                        .ifPresent(snapshotRepository::delete);
            } else {
                EquipmentSnapshotEntity snapshot = snapshotRepository
                        .findByEquipmentId(event.getEquipmentId())
                        .orElse(EquipmentSnapshotEntity.builder()
                                .equipmentId(event.getEquipmentId())
                                .build());

                snapshot.setName(event.getName());
                snapshot.setSerial(event.getSerial());
                snapshot.setLocationId(event.getLocationId());
                snapshot.setLocationName(event.getLocationName());
                snapshot.setState(event.getStateName());
                snapshot.setClassification(event.getClassificationName());
                snapshot.setBrand(event.getBrand());
                snapshot.setModel(event.getModel());
                snapshot.setRiskLevel(event.getRiskLevel());

                snapshotRepository.save(snapshot);
            }

            EquipmentHistorialEntity historial = EquipmentHistorialEntity.builder()
                    .equipmentId(event.getEquipmentId())
                    .eventType(event.getEventType())
                    .name(event.getName())
                    .serial(event.getSerial())
                    .locationId(event.getLocationId())
                    .locationName(event.getLocationName())
                    .stateName(event.getStateName())
                    .classificationName(event.getClassificationName())
                    .brand(event.getBrand())
                    .model(event.getModel())
                    .riskLevel(event.getRiskLevel())
                    .updatedBy(event.getUpdatedBy())
                    .timestamp(event.getTimestamp() != null ? event.getTimestamp() : java.time.LocalDateTime.now())
                    .build();

            historialRepository.save(historial);

            log.info("EquipmentEvent procesado: equipmentId={}, eventType={}", event.getEquipmentId(), event.getEventType());
        } catch (Exception e) {
            log.error("Error procesando EquipmentEvent: equipmentId={}, eventType={}",
                    event.getEquipmentId(), event.getEventType(), e);
        }
    }
}
