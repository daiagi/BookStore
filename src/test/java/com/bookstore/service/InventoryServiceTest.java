package com.bookstore.service;

import com.bookstore.model.Inventory;
import com.bookstore.repository.InventoryRepository;
import com.bookstore.websocket.WebSocketHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InventoryServiceTest {

    @InjectMocks
    private InventoryService inventoryService;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryReservationHandler inventoryReservationHandler;

    @Mock
    private WebSocketHandler webSocketHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetInventoryByBookId_NewInventory() {
        // Arrange
        int bookId = 1;
        when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.empty());

        // Act
        Inventory inventory = inventoryService.getInventoryByBookId(bookId);

        // Assert
        assertEquals(bookId, inventory.getBookId());
        verify(inventoryRepository, times(1)).findByBookId(bookId);
    }

    @Test
    void testGetInventoryByBookId_ExistingInventory() {
        // Arrange
        int bookId = 1;
        Inventory existingInventory = new Inventory(bookId);
        when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(existingInventory));

        // Act
        Inventory inventory = inventoryService.getInventoryByBookId(bookId);

        // Assert
        assertEquals(existingInventory, inventory);
        verify(inventoryRepository, times(1)).findByBookId(bookId);
    }

    @Test
    void testReserveStock_Success() {
        // Arrange
        int bookId = 1;
        int amount = 5;
        Inventory inventory = new Inventory(bookId);
        inventory.setStock(10);
        when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));
        when(inventoryReservationHandler.getReservedStock(bookId)).thenReturn(0);

        // Act
        boolean result = inventoryService.reserveStock(bookId, amount);

        // Assert
        assertTrue(result);
        verify(inventoryReservationHandler, times(1)).reserveStock(bookId, amount);
        verify(webSocketHandler, times(1)).notifyUsersAboutStockChange(bookId, 5);
    }

    @Test
    void testReserveStock_Failure() {
        // Arrange
        int bookId = 1;
        int amount = 5;
        Inventory inventory = new Inventory(bookId);
        inventory.setStock(3);
        when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));
        when(inventoryReservationHandler.getReservedStock(bookId)).thenReturn(0);

        // Act
        boolean result = inventoryService.reserveStock(bookId, amount);

        // Assert
        assertFalse(result);
        verify(inventoryReservationHandler, never()).reserveStock(anyInt(), anyInt());
        verify(webSocketHandler, never()).notifyUsersAboutStockChange(anyInt(), anyInt());
    }

    @Test
    void testReserveStock_InvalidAmount() {
        // Arrange
        int bookId = 1;
        int amount = -5;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> inventoryService.reserveStock(bookId, amount));
        assertEquals("Amount must be greater than 0", exception.getMessage());
    }

    @Test
    void testReleaseReservedStock_Success() {
        // Arrange
        int bookId = 1;
        int amount = 5;
        when(inventoryReservationHandler.getReservedStock(bookId)).thenReturn(10);

        // Act
        boolean result = inventoryService.releaseReservedStock(bookId, amount);

        // Assert
        assertTrue(result);
        verify(inventoryReservationHandler, times(1)).reduceReservedStock(bookId, amount);
        verify(webSocketHandler, times(1)).notifyUsersAboutStockChange(bookId, 5);
    }

    @Test
    void testReleaseReservedStock_Failure() {
        // Arrange
        int bookId = 1;
        int amount = 5;
        when(inventoryReservationHandler.getReservedStock(bookId)).thenReturn(3);

        // Act
        boolean result = inventoryService.releaseReservedStock(bookId, amount);

        // Assert
        assertFalse(result);
        verify(inventoryReservationHandler, never()).reduceReservedStock(anyInt(), anyInt());
        verify(webSocketHandler, never()).notifyUsersAboutStockChange(anyInt(), anyInt());
    }

    @Test
    void testReleaseReservedStock_InvalidAmount() {
        // Arrange
        int bookId = 1;
        int amount = -5;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> inventoryService.releaseReservedStock(bookId, amount));
        assertEquals("Amount must be greater than 0", exception.getMessage());
    }

    @Test
    void testUpdateStock() {
        // Arrange
        int bookId = 1;
        int stock = 10;
        Inventory inventory = new Inventory(bookId);
        when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        // Act
        Inventory updatedInventory = inventoryService.updateStock(bookId, stock);

        // Assert
        assertEquals(stock, updatedInventory.getStock());
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
        verify(webSocketHandler, times(1)).notifyUsersAboutStockChange(bookId, 10);
    }

    @Test
    void testUpdateStock_NegativeStock() {
        // Arrange
        int bookId = 1;
        int stock = -5;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> inventoryService.updateStock(bookId, stock));
        assertEquals("Stock cannot be negative", exception.getMessage());
    }

    @Test
    void testIncreaseStock() {
        // Arrange
        int bookId = 1;
        int amount = 5;
        Inventory inventory = new Inventory(bookId);
        inventory.setStock(10);
        when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        // Act
        Inventory updatedInventory = inventoryService.increaseStock(bookId, amount);

        // Assert
        assertEquals(15, updatedInventory.getStock());
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
        verify(webSocketHandler, times(1)).notifyUsersAboutStockChange(bookId, 15);
    }

    @Test
    void testIncreaseStock_InvalidAmount() {
        // Arrange
        int bookId = 1;
        int amount = -5;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> inventoryService.increaseStock(bookId, amount));
        assertEquals("Amount must be greater than 0", exception.getMessage());
    }

    @Test
    void testReduceStock_Success() {
        // Arrange
        int bookId = 1;
        int amount = 5;
        Inventory inventory = new Inventory(bookId);
        inventory.setStock(10);
        when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));
        when(inventoryReservationHandler.getReservedStock(bookId)).thenReturn(0);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        // Act
        boolean result = inventoryService.reduceStock(bookId, amount);

        // Assert
        assertTrue(result);
        assertEquals(5, inventory.getStock());
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
        verify(inventoryReservationHandler, times(1)).reduceReservedStock(bookId, amount);
        verify(webSocketHandler, times(1)).notifyUsersAboutStockChange(bookId, 5);
    }

    @Test
    void testReduceStock_Failure() {
        // Arrange
        int bookId = 1;
        int amount = 5;
        Inventory inventory = new Inventory(bookId);
        inventory.setStock(3);
        when(inventoryRepository.findByBookId(bookId)).thenReturn(Optional.of(inventory));
        when(inventoryReservationHandler.getReservedStock(bookId)).thenReturn(0);

        // Act
        boolean result = inventoryService.reduceStock(bookId, amount);

        // Assert
        assertFalse(result);
        assertEquals(3, inventory.getStock());
        verify(inventoryRepository, never()).save(any(Inventory.class));
        verify(inventoryReservationHandler, never()).reduceReservedStock(anyInt(), anyInt());
        verify(webSocketHandler, never()).notifyUsersAboutStockChange(anyInt(), anyInt());
    }

    @Test
    void testReduceStock_InvalidAmount() {
        // Arrange
        int bookId = 1;
        int amount = -5;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> inventoryService.reduceStock(bookId, amount));
        assertEquals("Amount must be greater than 0", exception.getMessage());
    }
}
