package com.glideclouds.springbootmongocrud.service;

import com.glideclouds.springbootmongocrud.event.UserCreatedEvent;
import com.glideclouds.springbootmongocrud.event.UserDeletedEvent;
import com.glideclouds.springbootmongocrud.event.UserUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishUserCreated(UserCreatedEvent event) {
        log.info("📤 Publishing User Created Event: userId={}, name={}, from server={}", 
            event.getUserId(), event.getName(), event.getServerPort());
        kafkaTemplate.send("user-created-topic", event.getUserId(), event);
    }

    public void publishUserUpdated(UserUpdatedEvent event) {
        log.info("📤 Publishing User Updated Event: userId={}, name={}, from server={}", 
            event.getUserId(), event.getName(), event.getServerPort());
        kafkaTemplate.send("user-updated-topic", event.getUserId(), event);
    }

    public void publishUserDeleted(UserDeletedEvent event) {
        log.info("📤 Publishing User Deleted Event: userId={}, from server={}", 
            event.getUserId(), event.getServerPort());
        kafkaTemplate.send("user-deleted-topic", event.getUserId(), event);
    }
}
