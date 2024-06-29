package com.bookstore.service;

import com.bookstore.model.Inventory;
import com.bookstore.repository.InventoryRepository;
import com.bookstore.websocket.WebSocketHandler;

import org.springframework.beans.factory.annotation.Autowired;
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

    private void notifyFrontend(int bookId) {
        Inventory inventory = getOrCreateInventory(bookId);
        int reservedStock = inventoryReservationHandler.getReservedStock(bookId);
        int availableStock = inventory.getStock() - reservedStock;
        webSocketHandler.notifyUsersAboutStockChange(bookId, availableStock);
    }

    public Inventory getInventoryByBookId(Integer bookId) {
        return getOrCreateInventory(bookId);
    }

    public synchronized boolean reserveStock(int bookId, int amount) {
        Inventory inventory = getOrCreateInventory(bookId);
        int currentStock = inventory.getStock();
        int reservedStock = inventoryReservationHandler.getReservedStock(bookId);
        int availableStock = currentStock - reservedStock;

        if (availableStock < amount) {
            return false;
        }

        inventoryReservationHandler.reserveStock(bookId, amount);
        notifyFrontend(bookId);
        return true;
    }

    public synchronized boolean releaseReservedStock(int bookId, int amount) {
        int reservedStock = inventoryReservationHandler.getReservedStock(bookId);

        if (reservedStock < amount) {
            return false;
        }

        inventoryReservationHandler.reduceReservedStock(bookId, amount);
        notifyFrontend(bookId);
        return true;
    }

    public synchronized Inventory updateStock(int bookId, int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        Inventory inventory = getOrCreateInventory(bookId);
        inventory.setStock(stock);
        Inventory updatedInventory = inventoryRepository.save(inventory);
        notifyFrontend(bookId);
        return updatedInventory;
    }

    public Inventory increaseStock(int bookId, int amount) {
        Inventory inventory = getOrCreateInventory(bookId);
        inventory.setStock(inventory.getStock() + amount);
        Inventory updatedInventory = inventoryRepository.save(inventory);
        notifyFrontend(bookId);
        return updatedInventory;
    }

    public synchronized boolean reduceStock(int bookId, int amount) {
        Inventory inventory = getOrCreateInventory(bookId);
        if (inventory.getStock() < amount) {
            return false;
        }
        inventory.setStock(inventory.getStock() - amount);
        inventoryReservationHandler.reduceReservedStock(bookId, amount);
        inventoryRepository.save(inventory);
        notifyFrontend(bookId);
        return true;
    }
}
