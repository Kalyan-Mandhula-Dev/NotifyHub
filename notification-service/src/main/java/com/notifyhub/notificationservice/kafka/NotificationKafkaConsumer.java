package com.notifyhub.notificationservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifyhub.notificationservice.dto.KafkaEventMessage;
import com.notifyhub.notificationservice.dto.response.EnrichmentResponse;
import com.notifyhub.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationKafkaConsumer {

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;
    private final PendingEnrichments pendingEnrichments;

    @KafkaListener(
            topics = "${kafka.topic.email}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeEmailEvent(ConsumerRecord<String, String> consumerRecord, Acknowledgment acknowledgment) {

        try {
            KafkaEventMessage message = objectMapper.readValue(consumerRecord.value(), KafkaEventMessage.class);
            notificationService.processNotification(message);

            acknowledgment.acknowledge();
            log.info("Email event acknowledged. offset: {}", consumerRecord.offset());

        } catch (Exception e) {
            log.error("Failed to process email event at offset: {} error: {}",
                    consumerRecord.offset(), e.getMessage());
        }
    }

    @KafkaListener(
            topics = "${kafka.topic.webhook}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeWebhookEvent(ConsumerRecord<String, String> consumerRecord, Acknowledgment acknowledgment) {

        log.info("Received webhook event from partition: {} offset: {}",
                consumerRecord.partition(), consumerRecord.offset());
        try {
            KafkaEventMessage message = objectMapper.readValue(consumerRecord.value(), KafkaEventMessage.class);
            notificationService.processNotification(message);

            acknowledgment.acknowledge();
            log.info("Webhook event acknowledged. offset: {}", consumerRecord.offset());
        } catch (Exception e) {
            log.error("Failed to process webhook event at offset: {} error: {}",
                    consumerRecord.offset(), e.getMessage());
        }
    }

    @KafkaListener(
            topics = "${kafka.topic.template.enrichment.res}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeEnrichmentResponseEvent(ConsumerRecord<String, String> consumerRecord, Acknowledgment acknowledgment) {

        log.info("Received enrichment response event from partition: {} offset: {}",
                consumerRecord.partition(), consumerRecord.offset());
        try {
            EnrichmentResponse message = objectMapper.readValue(consumerRecord.value(), EnrichmentResponse.class);

            boolean completed = pendingEnrichments.complete(message.getCorrelationId(), message.getTemplateHtml());

            if (!completed) {
                log.debug("Enrichment response arrived after timeout or was already handled. correlationId: {}", message.getCorrelationId());
            }

            acknowledgment.acknowledge();

            log.info("Enrichment response event acknowledged. offset: {}", consumerRecord.offset());
        } catch (Exception e) {
            log.error("Failed to process enrichment response event at offset: {} error: {}",
                    consumerRecord.offset(), e.getMessage());
        }
    }
}

