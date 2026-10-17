package com.flashsale.order.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${app.topics.orders}")
    private String orderTopic;

    /**
     * Programowe utworzenie tematu z 6 partycjami i 1 repliką.
     * 6 partycji pozwoli nam skalować konsumentów horyzontalnie aż do 6 instancji równolegle!
     */
    @Bean
    public NewTopic orderPlacedTopic() {
        return TopicBuilder.name(orderTopic)
                .partitions(6)
                .replicas(1)
                .build();
    }
}
