package sigebi.reportsandaudit.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import sigebi.reportsandaudit.entities.ConsolidatedReportViewEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import sigebi.reportsandaudit.repository.ConsolidatedReportViewRepository;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportEventConsumer {

    private final ConsolidatedReportViewRepository repository;
    private final ObjectMapper objectMapper; // 🔥 ESTE FALTABA

    @KafkaListener(
            topics = "${kafka.topics.report-events}",
            groupId = "sigebi-report-group"
    )
    public void consume(String message) {
        try {
            ReportEvent event = objectMapper.readValue(message, ReportEvent.class);

            ConsolidatedReportViewEntity entity = mapToEntity(event);

            repository.save(entity);

            log.info("Evento procesado correctamente: {}", event);

        } catch (Exception e) {
            log.error("Error procesando ReportEvent", e);
        }
    }

    private ConsolidatedReportViewEntity mapToEntity(ReportEvent event) {
        return ConsolidatedReportViewEntity.builder()
                .equipmentId(event.getEquipmentId())
                .location(event.getLocation())
                .maintenanceType(event.getMaintenanceType())
                .date(event.getDate() != null ? event.getDate() : LocalDate.now())
                .build();
    }
}