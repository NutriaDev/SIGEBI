package inventory.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovementEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.movement-events:sigebi-movement-events}")
    private String topic;

    public void send(MovementEvent event) {
        String key = event.getEquipmentId() != null
                ? event.getEquipmentId().toString()
                : "unknown";

        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Error enviando MovementEvent: {}", ex.getMessage());
                    } else {
                        log.info("MovementEvent enviado: equipmentId={}", event.getEquipmentId());
                    }
                });
    }
}