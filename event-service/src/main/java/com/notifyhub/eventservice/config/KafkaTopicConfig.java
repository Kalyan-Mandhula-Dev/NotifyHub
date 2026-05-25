package com.notifyhub.eventservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topic.email}")
    private String emailTopic;

    @Value("${kafka.topic.webhook}")
    private String webhookTopic;

    @Bean
    public NewTopic emailNotificationTopic() {
        return TopicBuilder.name(emailTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic webhookNotificationTopic() {
        return TopicBuilder.name(webhookTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
