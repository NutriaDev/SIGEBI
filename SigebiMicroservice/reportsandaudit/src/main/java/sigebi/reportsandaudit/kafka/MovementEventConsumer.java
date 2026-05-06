package sigebi.reportsandaudit.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import sigebi.reportsandaudit.entities.MovementReportViewEntity;
import sigebi.reportsandaudit.repository.MovementReportViewRepository;

import java.time.LocalDate;

// MovementEventConsumer.java
@Slf4j
@Service
@RequiredArgsConstructor
public class MovementEventConsumer {

    private final MovementReportViewRepository repository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${kafka.topics.movement-events}",
            groupId = "sigebi-report-group"
    )

    public void consume(MovementEvent event) {  // ← String → MovementEvent, eliminar ObjectMapper
        try {
            repository.save(MovementReportViewEntity.builder()
                    .equipmentId(event.getEquipmentId())
                    .originLocationId(event.getOriginLocationId())
                    .destinationLocationId(event.getDestinationLocationId())
                    .date(event.getDate() != null ? event.getDate() : LocalDate.now())
                    .responsibleUserName(event.getResponsibleUserName())
                    .build());

            log.info("MovementEvent procesado: {}", event);
        } catch (Exception e) {
            log.error("Error procesando MovementEvent", e);
        }
    }
}