package sigebi.reportsandaudit.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topics.audit-events:sigebi-audit-events}")
    private String auditEventsTopic;

    @Value("${kafka.topics.report-events:sigebi-report-events}")
    private String reportEventsTopic;

    @Value("${kafka.topics.service-report-events:sigebi-service-report-events}")
    private String serviceReportEventsTopic;

    @Bean
    public NewTopic auditEventsTopic() {
        return TopicBuilder.name(auditEventsTopic)
                .partitions(3)
                .replicas(1)
                .config("retention.ms", "604800000")
                .config("cleanup.policy", "delete")
                .build();
    }

    @Bean
    public NewTopic reportEventsTopic() {
        return TopicBuilder.name(reportEventsTopic)
                .partitions(3)
                .replicas(1)
                .config("retention.ms", "86400000")
                .config("cleanup.policy", "delete")
                .build();
    }

    @Bean
    public NewTopic serviceReportEventsTopic() {
        return TopicBuilder.name(serviceReportEventsTopic)
                .partitions(3)
                .replicas(1)
                .config("retention.ms", "86400000")
                .config("cleanup.policy", "delete")
                .build();
    }
}
