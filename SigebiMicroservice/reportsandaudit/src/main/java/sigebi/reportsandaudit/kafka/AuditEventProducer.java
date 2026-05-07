package sigebi.reportsandaudit.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class AuditEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.audit-events:sigebi-audit-events}")
    private String topic;

    public AuditEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAuditEvent(AuditEvent event) {
        String key = event.getUserId() != null ? event.getUserId().toString() : "unknown";

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Audit event enviado: userId={}, action={}, module={}, partition={}, offset={}",
                        event.getUserId(), event.getAction(), event.getModule(),
                        result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            } else {
                log.error("Error enviando audit event: {}", ex.getMessage(), ex);
            }
        });
    }
}
