package com.glideclouds.springbootmongocrud.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    @Value("${server.port}")
    private String currentServerPort;

    private final EventStreamService eventStreamService;

    @KafkaListener(topics = "user-created-topic", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserCreated(Map<String, Object> event) {
        String sourceServer = event.get("serverPort").toString();

        if (!sourceServer.equals(currentServerPort)) {
            log.info("🔔 ════════════════════════════════════════════════════════");
            log.info("🔔 USER CREATED EVENT RECEIVED ON SERVER [{}]", currentServerPort);
            log.info("🔔 Source Server: {}", sourceServer);
            log.info("🔔 User ID: {}", event.get("userId"));
            log.info("🔔 Name: {}", event.get("name"));
            log.info("🔔 Email: {}", event.get("email"));
            log.info("🔔 Timestamp: {}", event.get("timestamp"));
            log.info("🔔 ════════════════════════════════════════════════════════");

            // Push to SSE clients connected to this server
            eventStreamService.send("user-created", event);
        }
    }

    @KafkaListener(topics = "user-updated-topic", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserUpdated(Map<String, Object> event) {
        String sourceServer = event.get("serverPort").toString();

        if (!sourceServer.equals(currentServerPort)) {
            log.info("🔔 ════════════════════════════════════════════════════════");
            log.info("🔔 USER UPDATED EVENT RECEIVED ON SERVER [{}]", currentServerPort);
            log.info("🔔 Source Server: {}", sourceServer);
            log.info("🔔 User ID: {}", event.get("userId"));
            log.info("🔔 Name: {}", event.get("name"));
            log.info("🔔 Email: {}", event.get("email"));
            log.info("🔔 Timestamp: {}", event.get("timestamp"));
            log.info("🔔 ════════════════════════════════════════════════════════");

            // Push to SSE clients
            eventStreamService.send("user-updated", event);
        }
    }

    @KafkaListener(topics = "user-deleted-topic", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserDeleted(Map<String, Object> event) {
        String sourceServer = event.get("serverPort").toString();

        if (!sourceServer.equals(currentServerPort)) {
            log.info("🔔 ════════════════════════════════════════════════════════");
            log.info("🔔 USER DELETED EVENT RECEIVED ON SERVER [{}]", currentServerPort);
            log.info("🔔 Source Server: {}", sourceServer);
            log.info("🔔 User ID: {}", event.get("userId"));
            log.info("🔔 Timestamp: {}", event.get("timestamp"));
            log.info("🔔 ════════════════════════════════════════════════════════");

            // Push to SSE clients
            eventStreamService.send("user-deleted", event);
        }
    }
}
