package com.notifyhub.eventservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifyhub.eventservice.dto.KafkaEventMessage;
import com.notifyhub.eventservice.dto.request.TriggerEventRequest;
import com.notifyhub.eventservice.dto.response.EventResponse;
import com.notifyhub.eventservice.entity.ChannelType;
import com.notifyhub.eventservice.entity.Event;
import com.notifyhub.eventservice.entity.EventOutbox;
import com.notifyhub.eventservice.entity.EventStatus;
import com.notifyhub.eventservice.exception.SubscriptionNotFoundException;
import com.notifyhub.eventservice.exception.TenantNotFoundException;
import com.notifyhub.eventservice.kafka.KafkaProducer;
import com.notifyhub.eventservice.repository.EventOutboxRepository;
import com.notifyhub.eventservice.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventRepository eventRepository;
    private final UserClientService userClientService;
    private final EventOutboxRepository eventOutboxRepository;
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.email}")
    private String emailTopic;

    @Value("${kafka.topic.webhook}")
    private String webhookTopic;

    public EventResponse triggerEvent(TriggerEventRequest eventRequest) {
        if (!userClientService.tenantExists(eventRequest.getTenantId())) {
            throw new TenantNotFoundException(
                    "Tenant not found: " + eventRequest.getTenantId()
            );
        }

        if (!userClientService.hasActiveSubscription(
                eventRequest.getTenantId(),
                eventRequest.getChannel().name())) {
            throw new SubscriptionNotFoundException(
                    "Tenant " + eventRequest.getTenantId() +
                            " has no active subscription for channel: " + eventRequest.getChannel()
            );
        }

        Event event = new Event();
        event.setTenantId(eventRequest.getTenantId());
        event.setEventType(eventRequest.getEventType());
        event.setChannel(eventRequest.getChannel());
        event.setRecipient(eventRequest.getRecipient());
        event.setStatus(EventStatus.RECEIVED);

        try {
            event.setPayload(objectMapper.writeValueAsString(eventRequest.getPayload()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize payload");
        }


        Event savedEvent = eventRepository.save(event);

        String topic = savedEvent.getChannel().equals(ChannelType.EMAIL) ? emailTopic : webhookTopic;

        EventOutbox outbox = new EventOutbox();
        outbox.setEventId(savedEvent.getId());
        outbox.setKafkaTopic(topic);
        outbox.setPublished(false);
        eventOutboxRepository.save(outbox);

        publishToKafka(savedEvent, outbox, topic);

        return mapToResponse(savedEvent);
    }

    private void publishToKafka(Event event, EventOutbox eventOutbox, String topic) {

        try {
            KafkaEventMessage message = KafkaEventMessage.builder()
                    .eventId(event.getId())
                    .tenantId(event.getTenantId())
                    .eventType(event.getEventType())
                    .channel(event.getChannel())
                    .recipient(event.getRecipient())
                    .payload(objectMapper.readValue(
                            event.getPayload(),
                            objectMapper.getTypeFactory()
                                    .constructMapType(java.util.Map.class,
                                            String.class, Object.class)
                    ))
                    .timestamp(LocalDateTime.now())
                    .build();

            kafkaProducer.publish(topic, message);

            event.setStatus(EventStatus.PUBLISHED);
            eventRepository.save(event);

            eventOutbox.setPublished(true);
            eventOutboxRepository.save(eventOutbox);
        } catch (Exception e) {

            log.error("Failed to publish event {} to Kafka: {}",
                    event.getId(), e.getMessage());
            event.setStatus(EventStatus.FAILED);
            eventRepository.save(event);
        }
    }

    @Scheduled(fixedDelay = 3000)
    public void retryUnpublisedEvents() {
        List<EventOutbox> unPublishedEvents = eventOutboxRepository.findByPublished(false);

        if (unPublishedEvents.isEmpty()) {
            log.info("Outbox scheduler: found {} unpublished events",
                    unPublishedEvents.size());
        }

        for (EventOutbox eventOutbox : unPublishedEvents) {
            eventRepository.findById(eventOutbox.getEventId())
                    .ifPresent(event -> {
                        log.info("Retrying event: {}", event.getId());
                        publishToKafka(event, eventOutbox, eventOutbox.getKafkaTopic());
                    });
        }
    }

    public List<EventResponse> getEventsByTenant(String tenantId) {
        return eventRepository.findByTenantId(tenantId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private EventResponse mapToResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .tenantId(event.getTenantId())
                .eventType(event.getEventType())
                .channel(event.getChannel())
                .recipient(event.getRecipient())
                .status(event.getStatus())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
