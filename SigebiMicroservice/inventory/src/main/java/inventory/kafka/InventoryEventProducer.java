package inventory.kafka;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.inventory-events:sigebi-inventory-events}")
    private String topic;

    public void send(InventoryEvent event) {
        String key = event.getLocationId() != null
                ? event.getLocationId().toString()
                : "unknown";

        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Error enviando InventoryEvent: locationId={}, error={}",
                                event.getLocationId(), ex.getMessage());
                    } else {
                        log.info("InventoryEvent enviado: locationId={}, total={}",
                                event.getLocationId(), event.getTotalEquipments());
                    }
                });
    }
}
