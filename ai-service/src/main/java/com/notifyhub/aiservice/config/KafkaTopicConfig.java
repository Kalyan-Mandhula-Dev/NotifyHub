package com.notifyhub.aiservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topic.template.enrichment.res}")
    private String enrichmentResponseTopic;

    @Bean
    public NewTopic enrichmentResponse() {
        return TopicBuilder.name(enrichmentResponseTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
