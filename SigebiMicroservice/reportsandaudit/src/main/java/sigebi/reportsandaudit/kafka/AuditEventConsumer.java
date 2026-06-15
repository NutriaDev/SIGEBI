package sigebi.reportsandaudit.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import sigebi.reportsandaudit.entities.AuditEventEntity;
import sigebi.reportsandaudit.repository.AuditEventRepository;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditEventConsumer {

    private final AuditEventRepository auditEventRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${kafka.topics.audit-events:sigebi-audit-events}",
            groupId = "${kafka.consumer.group-id:sigebi-audit-group}"
    )
    public void consumeAuditEvent(ConsumerRecord<String, Object> record) {
        try {
            Object value = record.value();

            Map<String, Object> eventMap;
            if (value instanceof Map) {
                eventMap = (Map<String, Object>) value;
            } else {
                eventMap = objectMapper.convertValue(value, Map.class);
            }

            AuditEventEntity entity = AuditEventEntity.builder()
                    .eventType(getStringValue(eventMap, "action", "UNKNOWN"))
                    .payload(objectMapper.writeValueAsString(eventMap))
                    .build();

            auditEventRepository.save(entity);

            log.info("Audit event consumido y almacenado: action={}, module={}, userId={}",
                    getStringValue(eventMap, "action", "N/A"),
                    getStringValue(eventMap, "module", "N/A"),
                    eventMap.get("userId"));

        } catch (Exception e) {
            log.error("Error procesando audit event de Kafka: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(
            topics = "${kafka.topics.report-events:sigebi-report-events}",
            groupId = "${kafka.consumer.group-id:sigebi-report-group}"
    )
    public void consumeReportEvent(ConsumerRecord<String, Object> record) {
        try {
            Object value = record.value();

            Map<String, Object> eventMap;
            if (value instanceof Map) {
                eventMap = (Map<String, Object>) value;
            } else {
                eventMap = objectMapper.convertValue(value, Map.class);
            }

            AuditEventEntity entity = AuditEventEntity.builder()
                    .eventType("REPORT_" + getStringValue(eventMap, "status", "UNKNOWN"))
                    .payload(objectMapper.writeValueAsString(eventMap))
                    .build();

            auditEventRepository.save(entity);

            log.info("Report event consumido y almacenado: reportId={}, type={}, status={}",
                    eventMap.get("reportId"),
                    getStringValue(eventMap, "type", "N/A"),
                    getStringValue(eventMap, "status", "N/A"));

        } catch (Exception e) {
            log.error("Error procesando report event de Kafka: {}", e.getMessage(), e);
        }
    }

    private String getStringValue(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        return value != null ? value.toString() : defaultValue;
    }
}
