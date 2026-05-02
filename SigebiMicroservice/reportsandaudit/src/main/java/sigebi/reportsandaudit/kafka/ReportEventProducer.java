package sigebi.reportsandaudit.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class ReportEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.report-events:sigebi-report-events}")
    private String topic;

    public ReportEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendReportEvent(ReportEvent event) {

        String key = event.getEquipmentId() != null
                ? event.getEquipmentId().toString()
                : "unknown";

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Report event enviado: equipmentId={}, type={}, partition={}, offset={}",
                        event.getEquipmentId(),
                        event.getEventType(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Error enviando report event: {}", ex.getMessage(), ex);
            }
        });
    }
}
