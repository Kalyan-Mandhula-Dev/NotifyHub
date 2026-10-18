package com.notifyhub.aiservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifyhub.aiservice.dto.request.EnrichmentRequest;
import com.notifyhub.aiservice.service.GeminiServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AIKafkaConsumer {

    private final ObjectMapper objectMapper;
    private final GeminiServiceClient geminiServiceClient;

    @KafkaListener(
            topics = "${kafka.topic.template.enrichment.req}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeEnrichmentRequest(ConsumerRecord<String, String> consumerRecord) {

        try {
            EnrichmentRequest message = objectMapper.readValue(consumerRecord.value(), EnrichmentRequest.class);
            geminiServiceClient.enrichTemplate(message);
        } catch (Exception e) {
            log.error("Failed to process enrichment request event at offset: {} error: {}",
                    consumerRecord.offset(), e.getMessage());
        }

    }
}
