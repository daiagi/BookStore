package com.bookstore.service;

import com.bookstore.model.Inventory;
import com.bookstore.repository.InventoryRepository;
import com.bookstore.websocket.WebSocketHandler;

import org.springframework.stereotype.Service;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final InventoryReservationHandler inventoryReservationHandler;
    private final WebSocketHandler webSocketHandler;

    public InventoryService(InventoryRepository inventoryRepository,
            InventoryReservationHandler inventoryReservationHandler, WebSocketHandler webSocketHandler) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryReservationHandler = inventoryReservationHandler;
        this.webSocketHandler = webSocketHandler;
    }

    private Inventory getOrCreateInventory(Integer bookId) {
        return inventoryRepository.findByBookId(bookId).orElseGet(() -> new Inventory(bookId));
    }

    private void notifyFrontend(int bookId, int availableStock) {

        webSocketHandler.notifyUsersAboutStockChange(bookId, availableStock);
    }

    public enum ErrorMessages {
        INVALID_AMOUNT("Amount must be greater than 0"),
        NEGATIVE_STOCK("Stock cannot be negative");

        private final String message;

        ErrorMessages(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return this.message;
        }

        public String getMessage() {
            return this.message;
        }
    }

    public Inventory getInventoryByBookId(Integer bookId) {
        return getOrCreateInventory(bookId);
    }

    public synchronized boolean reserveStock(int bookId, int amount) throws IllegalArgumentException {

        if (amount <= 0) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_AMOUNT.getMessage());
        }
        Inventory inventory = getOrCreateInventory(bookId);
        int currentStock = inventory.getStock();
        int reservedStock = inventoryReservationHandler.getReservedStock(bookId);
        int availableStock = currentStock - reservedStock;

        if (availableStock < amount) {
            return false;
        }

        inventoryReservationHandler.reserveStock(bookId, amount);
        notifyFrontend(bookId, availableStock - amount);
        return true;
    }

    public synchronized boolean releaseReservedStock(int bookId, int amount) throws IllegalArgumentException {
        if (amount <= 0) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_AMOUNT.getMessage());
        }
        int reservedStock = inventoryReservationHandler.getReservedStock(bookId);

        if (reservedStock < amount) {
            return false;
        }
        Inventory inventory = getOrCreateInventory(bookId);
        int currentStock = inventory.getStock();

        inventoryReservationHandler.reduceReservedStock(bookId, amount);
        notifyFrontend(bookId, currentStock + amount);
        return true;
    }

    public synchronized Inventory updateStock(int bookId, int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException(ErrorMessages.NEGATIVE_STOCK.getMessage());
        }
        Inventory inventory = getOrCreateInventory(bookId);
        inventory.setStock(stock);
        Inventory updatedInventory = inventoryRepository.save(inventory);
        notifyFrontend(bookId, stock) ;
        return updatedInventory;
    }

    public Inventory increaseStock(int bookId, int amount) throws IllegalArgumentException {
        if (amount <= 0) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_AMOUNT.getMessage());
        }
        Inventory inventory = getOrCreateInventory(bookId);
        int updatedStock = inventory.getStock() + amount;
        inventory.setStock(updatedStock);
        Inventory updatedInventory = inventoryRepository.save(inventory);
        notifyFrontend(bookId, updatedStock);
        return updatedInventory;
    }

    public synchronized boolean reduceStock(int bookId, int amount) throws IllegalArgumentException {
        if (amount <= 0) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_AMOUNT.getMessage());
        }
        Inventory inventory = getOrCreateInventory(bookId);
        if (inventory.getStock() < amount) {
            return false;
        }
        int updatedStock = inventory.getStock() - amount;
        inventory.setStock(updatedStock);
        inventoryReservationHandler.reduceReservedStock(bookId, amount);
        inventoryRepository.save(inventory);
        notifyFrontend(bookId, updatedStock);
        return true;
    }
}
