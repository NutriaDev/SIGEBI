package sigebi.reportsandaudit.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import sigebi.reportsandaudit.entities.InventoryReportViewEntity;
import sigebi.reportsandaudit.repository.InventoryReportViewRepository;

import java.time.LocalDate;

// InventoryEventConsumer.java
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryEventConsumer {

    private final InventoryReportViewRepository repository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${kafka.topics.inventory-events}",
            groupId = "sigebi-report-group"
    )
    public void consume(String message) {
        try {
            InventoryEvent event = objectMapper.readValue(message, InventoryEvent.class);

            repository.save(InventoryReportViewEntity.builder()
                    .locationId(event.getLocationId())
                    .locationName(event.getLocationName())
                    .date(event.getDate() != null ? event.getDate() : LocalDate.now())
                    .totalEquipments(event.getTotalEquipments())
                    .activeEquipments(event.getActiveEquipments())
                    .inactiveEquipments(event.getInactiveEquipments())
                    .build());

            log.info("InventoryEvent procesado: {}", event);
        } catch (Exception e) {
            log.error("Error procesando InventoryEvent", e);
        }
    }
}
