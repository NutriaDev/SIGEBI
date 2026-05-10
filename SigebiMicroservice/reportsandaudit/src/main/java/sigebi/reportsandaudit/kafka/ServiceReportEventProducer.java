package sigebi.reportsandaudit.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class ServiceReportEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.service-report-events:sigebi-service-report-events}")
    private String topic;

    public ServiceReportEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendServiceReportEvent(MaintenanceServiceReportCreatedEvent event) {
        String key = event.getMaintenanceId() != null
                ? event.getMaintenanceId().toString()
                : "unknown";

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Service report event enviado: maintenanceId={}, reportId={}, partition={}, offset={}",
                        event.getMaintenanceId(), event.getReportId(),
                        result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            } else {
                log.error("Error enviando service report event: {}", ex.getMessage(), ex);
            }
        });
    }
}
