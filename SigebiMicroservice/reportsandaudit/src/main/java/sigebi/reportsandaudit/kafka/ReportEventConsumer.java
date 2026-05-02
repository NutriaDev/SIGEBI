package sigebi.reportsandaudit.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import sigebi.reportsandaudit.entities.ConsolidatedReportViewEntity;

import sigebi.reportsandaudit.repository.ConsolidatedReportViewRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReportEventConsumer {

    private final ConsolidatedReportViewRepository repository;

    @KafkaListener(
            topics = "${kafka.topics.report-events}",
            groupId = "sigebi-report-group"
    )
    public void consume(ReportEvent event) {

        ConsolidatedReportViewEntity entity =
                mapToEntity(event);

        repository.save(entity);
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