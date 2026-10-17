package com.flashsale.payment.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${app.topics.payments}")
    private String paymentsTopic;

    /**
     * Temat na zdarzenia udanych płatności (6 partycji).
     */
    @Bean
    public NewTopic paymentCompletedTopic() {
        return TopicBuilder.name(paymentsTopic)
                .partitions(6)
                .replicas(1)
                .build();
    }
}
