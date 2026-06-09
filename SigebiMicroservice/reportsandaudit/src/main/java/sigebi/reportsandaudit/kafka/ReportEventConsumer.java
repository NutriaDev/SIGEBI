package sigebi.reportsandaudit.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import sigebi.reportsandaudit.entities.ConsolidatedReportViewEntity;
import lombok.extern.slf4j.Slf4j;

import sigebi.reportsandaudit.entities.MaintenanceReportViewEntity;
import sigebi.reportsandaudit.repository.ConsolidatedReportViewRepository;

import java.time.LocalDate;
import sigebi.reportsandaudit.repository.MaintenanceReportViewRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportEventConsumer {

    private final ConsolidatedReportViewRepository consolidatedRepository;
    private final MaintenanceReportViewRepository maintenanceRepository;


    @KafkaListener(
            topics = "${kafka.topics.report-events}",
            groupId = "sigebi-report-group"
    )
    public void consume(ReportEvent event) { // ← String → ReportEvent
        try {
            consolidatedRepository.save(
                    ConsolidatedReportViewEntity.builder()
                            .equipmentId(event.getEquipmentId())

                            // Equipo
                            .equipmentName(event.getEquipmentName())
                            .brand(event.getBrand())
                            .model(event.getModel())
                            .serial(event.getSerial())
                            .inventoryCode(event.getInventoryCode())

                            // Ubicaciones
                            .physicalLocation(event.getPhysicalLocation())
                            .processLocation(event.getProcessLocation())

                            // Mantenimiento
                            .maintenanceType(event.getMaintenanceType())
                            .technicalDiagnosis(event.getTechnicalDiagnosis())
                            .servicePerformed(event.getServicePerformed())
                            .failureCause(event.getFailureCause())

                            // Observaciones
                            .observations(event.getObservations())

                            .date(event.getDate() != null
                                    ? event.getDate()
                                    : LocalDate.now())
                            .build()
            );
            maintenanceRepository.save(MaintenanceReportViewEntity.builder()
                    .equipmentId(event.getEquipmentId())
                    .type(event.getMaintenanceType())
                    .status(event.getStatus())
                    .date(event.getDate() != null ? event.getDate() : LocalDate.now())
                    .technicianName(event.getTechnicianName())
                    .build());

            log.info("ReportEvent procesado: {}", event);
        } catch (Exception e) {
            log.error("Error procesando ReportEvent", e);
        }
    }
}