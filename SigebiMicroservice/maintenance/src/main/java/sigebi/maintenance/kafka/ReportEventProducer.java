package sigebi.maintenance.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.report-events:sigebi-report-events}")
    private String topic;

    public void send(ReportEvent event) {

        String key = event.getEquipmentId() != null
                ? event.getEquipmentId().toString()
                : "unknown";

        kafkaTemplate.send(topic, key, event);

        log.info("Evento enviado a Kafka: type={}, equipmentId={}",
                event.getEventType(), event.getEquipmentId());
    }
}
