package com.notifyhub.eventservice.entity;

public enum EventStatus {
    RECEIVED,  // received and stored in db, not sent to kafka yet
    PUBLISHED, // published to kafka successfully
    FAILED     // failed to publish
}
