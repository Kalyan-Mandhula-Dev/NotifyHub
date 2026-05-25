package com.notifyhub.notificationservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifyhub.notificationservice.dto.KafkaEventMessage;
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
}

