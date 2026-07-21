package com.notifyhub.eventservice.controller;

import com.notifyhub.eventservice.dto.request.TriggerEventRequest;
import com.notifyhub.eventservice.dto.response.EventResponse;
import com.notifyhub.eventservice.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/trigger")
    public ResponseEntity<EventResponse> triggerEvent(@Valid @RequestBody TriggerEventRequest eventRequest,
                                                      @RequestHeader(value = "X-Tenant-Id", required = false) String tenantIdFromGateway){

        if(tenantIdFromGateway != null){
            eventRequest.setTenantId(tenantIdFromGateway);
        }

        EventResponse eventResponse = eventService.triggerEvent(eventRequest);
        return new ResponseEntity<>(eventResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<List<EventResponse>> getEvents(
            @PathVariable String tenantId) {
        return ResponseEntity.ok(eventService.getEventsByTenant(tenantId));
    }
}
