package com.notifyhub.notificationservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topic.template.enrichment.req}")
    private String templateEnrichmentReqTopic;


    @Bean
    public NewTopic templateEnrichmentRequest(){
        return TopicBuilder.name(templateEnrichmentReqTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
