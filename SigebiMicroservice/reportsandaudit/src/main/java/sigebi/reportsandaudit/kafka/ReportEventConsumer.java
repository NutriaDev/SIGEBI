package sigebi.reportsandaudit.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import sigebi.reportsandaudit.entities.ConsolidatedReportViewEntity;
import lombok.extern.slf4j.Slf4j;

import sigebi.reportsandaudit.entities.MaintenanceReportViewEntity;
import sigebi.reportsandaudit.repository.ConsolidatedReportViewRepository;

import java.time.LocalDate;

import sigebi.reportsandaudit.repository.EquipmentSnapshotRepository;
import sigebi.reportsandaudit.repository.MaintenanceReportViewRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportEventConsumer {

    private final ConsolidatedReportViewRepository consolidatedRepository;
    private final MaintenanceReportViewRepository maintenanceRepository;
    private final EquipmentSnapshotRepository snapshotRepository;


    @KafkaListener(
            topics = "${kafka.topics.report-events}",
            groupId = "sigebi-report-group"
    )
    public void consume(ReportEvent event) {

        try {

            // =====================================
            // ACTUALIZAR REPORTE TÉCNICO
            // =====================================
            if ("SERVICE_REPORT".equals(event.getEventType())) {

                consolidatedRepository
                        .findByMaintenanceId(event.getMaintenanceId())
                        .ifPresent(row -> {

                            row.setServiceReportId(
                                    event.getServiceReportId()
                            );

                            consolidatedRepository.save(row);
                        });

                log.info(
                        "SERVICE_REPORT actualizado maintenanceId={}, serviceReportId={}",
                        event.getMaintenanceId(),
                        event.getServiceReportId()
                );

                return;
            }

            // =====================================
            // CREAR REGISTRO DE MANTENIMIENTO
            // =====================================
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
                            .maintenanceStatus(event.getMaintenanceStatus())
                            .maintenanceId(event.getMaintenanceId())

                            // Inicialmente sin PDF
                            .serviceReportId(null)

                            // Observaciones
                            .observations(event.getObservations())

                            .date(
                                    event.getDate() != null
                                            ? event.getDate()
                                            : LocalDate.now()
                            )
                            .build()
            );
            log.info("STATUS EVENTO = {}", event.getMaintenanceStatus());

            maintenanceRepository.save(
                    MaintenanceReportViewEntity.builder()
                            .equipmentId(event.getEquipmentId())
                            .type(event.getMaintenanceType())
                            .status(event.getMaintenanceStatus())
                            .date(
                                    event.getDate() != null
                                            ? event.getDate()
                                            : LocalDate.now()
                            )
                            .technicianName(event.getTechnicianName())
                            .build()
            );

            snapshotRepository
                    .findByEquipmentId(event.getEquipmentId())
                    .ifPresent(snapshot -> {

                        LocalDate maintenanceDate =
                                event.getDate() != null
                                        ? event.getDate()
                                        : LocalDate.now();

                        snapshot.setLastMaintenanceDate(maintenanceDate);

                        snapshotRepository.save(snapshot);

                        log.info(
                                "Snapshot actualizado. equipmentId={}, lastMaintenanceDate={}",
                                event.getEquipmentId(),
                                maintenanceDate
                        );
                    });

            log.info("MAINTENANCE procesado: {}", event);

        } catch (Exception e) {

            log.error("Error procesando ReportEvent", e);

        }
    }
}