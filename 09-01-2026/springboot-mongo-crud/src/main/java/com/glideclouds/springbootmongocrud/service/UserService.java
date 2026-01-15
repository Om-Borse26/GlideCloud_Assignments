package com.glideclouds.springbootmongocrud.service;

import com.glideclouds.springbootmongocrud.event.UserCreatedEvent;
import com.glideclouds.springbootmongocrud.event.UserDeletedEvent;
import com.glideclouds.springbootmongocrud.event.UserUpdatedEvent;
import com.glideclouds.springbootmongocrud.model.User;
import com.glideclouds.springbootmongocrud.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;
    
    @Value("${server.port}")
    private String serverPort;

    public User createUser(User user) {
        User savedUser = userRepository.save(user);
        
        // Publish Kafka event
        UserCreatedEvent event = new UserCreatedEvent(
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getEmail(),
            serverPort,
            System.currentTimeMillis()
        );
        kafkaProducerService.publishUserCreated(event);
        
        return savedUser;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User updateUser(String id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        User updatedUser = userRepository.save(existingUser);
        
        // Publish Kafka event
        UserUpdatedEvent event = new UserUpdatedEvent(
            updatedUser.getId(),
            updatedUser.getName(),
            updatedUser.getEmail(),
            serverPort,
            System.currentTimeMillis()
        );
        kafkaProducerService.publishUserUpdated(event);
        
        return updatedUser;
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
        
        // Publish Kafka event
        UserDeletedEvent event = new UserDeletedEvent(
            id,
            serverPort,
            System.currentTimeMillis()
        );
        kafkaProducerService.publishUserDeleted(event);
    }
}
