package com.bookstore.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryReservationHandlerTest {

    @InjectMocks
    private InventoryReservationHandler inventoryReservationHandler;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testReserveStock_Success() {
        // Arrange
        int bookId = 1;
        int amount = 5;

        // Act
        inventoryReservationHandler.reserveStock(bookId, amount);

        // Assert
        verify(valueOperations, times(1)).increment("reserved_stock:" + bookId, amount);
        verify(redisTemplate, times(1)).expire("reserved_stock:" + bookId, 10, TimeUnit.MINUTES);
        verify(valueOperations, times(1)).set(eq("reservation_timestamp:" + bookId), any(LocalDateTime.class));
        verify(redisTemplate, times(1)).expire("reservation_timestamp:" + bookId, 10, TimeUnit.MINUTES);
    }

    @Test
    void testReserveStock_InvalidAmount() {
        // Arrange
        int bookId = 1;
        int amount = -5;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> inventoryReservationHandler.reserveStock(bookId, amount));
        assertEquals("Amount must be greater than 0", exception.getMessage());
    }

    @Test
    void testIncreaseReservedStock_Success() {
        // Arrange
        int bookId = 1;
        int amount = 5;

        // Act
        inventoryReservationHandler.increaseReservedStock(bookId, amount);

        // Assert
        verify(valueOperations, times(1)).increment("reserved_stock:" + bookId, amount);
        verify(redisTemplate, times(1)).expire("reserved_stock:" + bookId, 10, TimeUnit.MINUTES);
    }

    @Test
    void testIncreaseReservedStock_InvalidAmount() {
        // Arrange
        int bookId = 1;
        int amount = -5;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> inventoryReservationHandler.increaseReservedStock(bookId, amount));
        assertEquals("Amount must be greater than 0", exception.getMessage());
    }

    @Test
    void testReduceReservedStock_Success() {
        // Arrange
        int bookId = 1;
        int amount = 5;

        // Act
        inventoryReservationHandler.reduceReservedStock(bookId, amount);

        // Assert
        verify(valueOperations, times(1)).increment("reserved_stock:" + bookId, -amount);
    }

    @Test
    void testReduceReservedStock_InvalidAmount() {
        // Arrange
        int bookId = 1;
        int amount = -5;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> inventoryReservationHandler.reduceReservedStock(bookId, amount));
        assertEquals("Amount must be greater than 0", exception.getMessage());
    }

    @Test
    void testGetReservedStock() {
        // Arrange
        int bookId = 1;
        when(valueOperations.get("reserved_stock:" + bookId)).thenReturn(10);

        // Act
        int reservedStock = inventoryReservationHandler.getReservedStock(bookId);

        // Assert
        assertEquals(10, reservedStock);
    }

    @Test
    void testSetReservationTimestamp() {
        // Arrange
        int bookId = 1;
        LocalDateTime timestamp = LocalDateTime.now();

        // Act
        inventoryReservationHandler.setReservationTimestamp(bookId, timestamp);

        // Assert
        verify(valueOperations, times(1)).set("reservation_timestamp:" + bookId, timestamp);
        verify(redisTemplate, times(1)).expire("reservation_timestamp:" + bookId, 10, TimeUnit.MINUTES);
    }

    @Test
    void testGetReservationTimestamp() {
        // Arrange
        int bookId = 1;
        LocalDateTime timestamp = LocalDateTime.now();
        when(valueOperations.get("reservation_timestamp:" + bookId)).thenReturn(timestamp);

        // Act
        LocalDateTime result = inventoryReservationHandler.getReservationTimestamp(bookId);

        // Assert
        assertEquals(timestamp, result);
    }
}
