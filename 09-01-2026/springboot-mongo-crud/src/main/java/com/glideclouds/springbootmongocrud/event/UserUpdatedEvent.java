package com.glideclouds.springbootmongocrud.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatedEvent {
    private String userId;
    private String name;
    private String email;
    private String serverPort;
    private long timestamp;
}
