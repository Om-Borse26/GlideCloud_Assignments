package com.glideclouds.springbootmongocrud.service;

import com.glideclouds.springbootmongocrud.model.User;
import com.glideclouds.springbootmongocrud.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("123");
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setAge(25);
    }

    @Test
    void testCreateUser_Success() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.createUser(testUser);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testGetAllUsers_Success() {
        // Arrange
        User user2 = new User();
        user2.setName("Jane Doe");
        user2.setEmail("jane@example.com");
        user2.setAge(30);

        List<User> users = Arrays.asList(testUser, user2);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById_Success() {
        // Arrange
        when(userRepository.findById("123")).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getUserById("123");

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(userRepository, times(1)).findById("123");
    }

    @Test
    void testGetUserById_NotFound() {
        // Arrange
        when(userRepository.findById("999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            userService.getUserById("999");
        });
        verify(userRepository, times(1)).findById("999");
    }

    @Test
    void testUpdateUser_Success() {
        // Arrange
        User updatedUser = new User();
        updatedUser.setName("John Updated");
        updatedUser.setEmail("john.updated@example.com");
        updatedUser.setAge(26);

        when(userRepository.findById("123")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.updateUser("123", updatedUser);

        // Assert
        assertNotNull(result);
        assertEquals("John Updated", testUser.getName());
        assertEquals("john.updated@example.com", testUser.getEmail());
        assertEquals(26, testUser.getAge());
        verify(userRepository, times(1)).findById("123");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testUpdateUser_NotFound() {
        // Arrange
        User updatedUser = new User();
        updatedUser.setName("John Updated");
        updatedUser.setEmail("john.updated@example.com");
        updatedUser.setAge(26);

        when(userRepository.findById("999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            userService.updateUser("999", updatedUser);
        });
        verify(userRepository, times(1)).findById("999");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser_Success() {
        // Arrange
        when(userRepository.existsById("123")).thenReturn(true);
        doNothing().when(userRepository).deleteById("123");

        // Act
        userService.deleteUser("123");

        // Assert
        verify(userRepository, times(1)).existsById("123");
        verify(userRepository, times(1)).deleteById("123");
    }

    @Test
    void testDeleteUser_NotFound() {
        // Arrange
        when(userRepository.existsById("999")).thenReturn(false);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            userService.deleteUser("999");
        });
        verify(userRepository, times(1)).existsById("999");
        verify(userRepository, never()).deleteById(anyString());
    }
}
