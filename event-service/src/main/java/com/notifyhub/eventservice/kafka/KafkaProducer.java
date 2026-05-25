package com.notifyhub.eventservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifyhub.eventservice.dto.KafkaEventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publish(String topic, KafkaEventMessage message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message.getTenantId(), jsonMessage);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Published to topic: {} partition: {} offset: {}",
                            topic,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish to topic: {} error: {}",
                            topic, ex.getMessage());
                }
            });
        } catch (Exception e) {
            log.error("Error serializing kafka message: {}", e.getMessage());
            throw new RuntimeException("Failed to publish event to Kafka", e);
        }
    }
}
