package equipment.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EquipmentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.equipment-events:sigebi-equipment-events}")
    private String topic;

    public void send(EquipmentEvent event) {
        String key = event.getEquipmentId() != null
                ? event.getEquipmentId().toString()
                : "unknown";

        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Error enviando EquipmentEvent: equipmentId={}, error={}",
                                event.getEquipmentId(), ex.getMessage());
                    } else {
                        log.info("EquipmentEvent enviado: equipmentId={}, eventType={}",
                                event.getEquipmentId(), event.getEventType());
                    }
                });
    }
}
