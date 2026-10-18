package com.notifyhub.aiservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifyhub.aiservice.dto.response.EnrichmentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AIKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(String topic, EnrichmentResponse enrichmentResponse) {
        try {
            String message = objectMapper.writeValueAsString(enrichmentResponse);
            SendResult<String, String> result = kafkaTemplate.send(topic, enrichmentResponse.getTenantId(), message).get();
            log.info("Published to topic: {} partition: {} offset: {}",
                    topic,
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
        } catch (Exception ex) {
            log.error("Failed to publish to topic: {} error: {}",
                    topic, ex.getMessage());
            throw new RuntimeException(
                    "Failed to publish event to Kafka", ex
            );
        }
    }
}
