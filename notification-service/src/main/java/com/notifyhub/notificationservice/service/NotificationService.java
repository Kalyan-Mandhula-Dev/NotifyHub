package com.notifyhub.notificationservice.service;

import com.notifyhub.notificationservice.dto.request.EnrichmentRequest;
import com.notifyhub.notificationservice.exception.EnrichmentFailedException;
import com.notifyhub.notificationservice.kafka.KafkaProducer;
import com.notifyhub.notificationservice.kafka.PendingEnrichments;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.notifyhub.notificationservice.dto.response.DeliveryLogResponse;
import com.notifyhub.notificationservice.dto.response.DeliveryStatsResponse;
import com.notifyhub.notificationservice.dto.KafkaEventMessage;
import com.notifyhub.notificationservice.entity.ChannelType;
import com.notifyhub.notificationservice.entity.DeliveryLog;
import com.notifyhub.notificationservice.entity.DeliveryStatus;
import com.notifyhub.notificationservice.repository.DeliveryLogRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private static final long ENRICHMENT_TIMEOUT_MS = 20000;
    private final DeliveryLogRepository deliveryLogRepository;
    private final EmailService emailService;
    private final WebhookService webhookService;
    private final TemplateServiceClient templateServiceClient;
    private final TemplateEngine templateEngine;
    private final AIServiceClient aiServiceClient;
    private final KafkaProducer kafkaProducer;
    private final PendingEnrichments pendingEnrichments;

    @Value("${kafka.topic.template.enrichment.req}")
    private String templateEnrichmentReqTopic;

    @Transactional
    public void processNotification(KafkaEventMessage message) {

        DeliveryLog deliveryLog = new DeliveryLog();
        deliveryLog.setEventId(message.getEventId());
        deliveryLog.setTenantId(message.getTenantId());
        deliveryLog.setEventType(message.getEventType());
        deliveryLog.setChannel(message.getChannel());
        deliveryLog.setRecipient(message.getRecipient());
        deliveryLog.setStatus(DeliveryStatus.PENDING);
        DeliveryLog savedLog = deliveryLogRepository.save(deliveryLog);

        try {
            if (message.getChannel() == ChannelType.EMAIL) {
                processEmail(message);
            } else if (message.getChannel() == ChannelType.WEBHOOK) {
                processWebhook(message);
            }

            savedLog.setStatus(DeliveryStatus.SENT);
            savedLog.setSentAt(LocalDateTime.now());
            deliveryLogRepository.save(savedLog);

            log.info("Notification delivered. eventId: {} channel: {}",
                    message.getEventId(), message.getChannel());

        } catch (Exception e) {
            savedLog.setStatus(DeliveryStatus.FAILED);
            savedLog.setErrorMessage(e.getMessage());
            deliveryLogRepository.save(savedLog);

            log.error("Notification failed. eventId: {} error: {}",
                    message.getEventId(), e.getMessage());

            throw new RuntimeException(
                    "Notification processing failed: " + e.getMessage(), e
            );
        }
    }

    public void processEmail(KafkaEventMessage message) throws Exception {
        String htmlContent;
        String template = templateServiceClient.fetchTemplate(
                message.getTenantId(),
                message.getEventType(),
                message.getPayload()
        ).get();

        try {
            htmlContent = enrichTemplate(message, template);
        } catch (EnrichmentFailedException ex) {
            log.error("AI failed to enrich the template");
            htmlContent = templateEngine.populate(template, message.getPayload());
        }

        String subject = formatSubject(message.getEventType());
        emailService.sendEmail(message.getRecipient(), subject, htmlContent);
    }

    public String enrichTemplate(KafkaEventMessage message, String template) {

        String finalTemplate;

        String correlationID = message.getTenantId() + ":" + message.getEventId();
        EnrichmentRequest enrichmentRequest = new EnrichmentRequest();
        enrichmentRequest.setCorrelationId(correlationID);
        enrichmentRequest.setTenantId(message.getTenantId());
        enrichmentRequest.setEventId(message.getEventId());
        enrichmentRequest.setEventType(message.getEventType());
        enrichmentRequest.setPayload(message.getPayload());
        enrichmentRequest.setTemplateHtml(template);

        try {

            CompletableFuture<String> fut = pendingEnrichments.register(correlationID);
            kafkaProducer.publish(templateEnrichmentReqTopic, enrichmentRequest);

            try {
                finalTemplate = fut.get(ENRICHMENT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new EnrichmentFailedException(ex.getMessage());
            } catch (TimeoutException | ExecutionException ex) {
                throw new EnrichmentFailedException(ex.getMessage());
            }

            return finalTemplate;
        } catch (EnrichmentFailedException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new EnrichmentFailedException(ex.getMessage());
        } finally {
            pendingEnrichments.remove(correlationID);
        }
    }

    private void processWebhook(KafkaEventMessage message) throws Exception {
        webhookService.sendWebhook(message.getRecipient(), message.getPayload());
    }

    private String formatSubject(String eventType) {
        return java.util.Arrays.stream(eventType.split("\\."))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }

    public List<DeliveryLogResponse> getDeliveryHistory(String tenantId) {
        return deliveryLogRepository.findByTenantId(tenantId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public DeliveryStatsResponse getStats(String tenantId) {
        return DeliveryStatsResponse.builder()
                .tenantId(tenantId)
                .totalSent(deliveryLogRepository
                        .countByTenantIdAndStatus(tenantId, DeliveryStatus.SENT))
                .totalFailed(deliveryLogRepository
                        .countByTenantIdAndStatus(tenantId, DeliveryStatus.FAILED))
                .totalPending(deliveryLogRepository
                        .countByTenantIdAndStatus(tenantId, DeliveryStatus.PENDING))
                .build();
    }

    private DeliveryLogResponse mapToResponse(DeliveryLog dl) {
        return DeliveryLogResponse.builder()
                .id(dl.getId())
                .eventId(dl.getEventId())
                .tenantId(dl.getTenantId())
                .eventType(dl.getEventType())
                .channel(dl.getChannel())
                .recipient(dl.getRecipient())
                .status(dl.getStatus())
                .errorMessage(dl.getErrorMessage())
                .sentAt(dl.getSentAt())
                .createdAt(dl.getCreatedAt())
                .build();
    }
}