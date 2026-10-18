package com.notifyhub.notificationservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifyhub.notificationservice.dto.request.EnrichmentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {


    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(String topic, EnrichmentRequest enrichmentRequest) {

        try {

            String message = objectMapper.writeValueAsString(enrichmentRequest);
            SendResult<String, String> result = kafkaTemplate.send(topic, enrichmentRequest.getTenantId(), message).get();

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
