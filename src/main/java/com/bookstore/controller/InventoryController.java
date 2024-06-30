package com.bookstore.controller;

import com.bookstore.model.Inventory;
import com.bookstore.service.InventoryService;

import io.swagger.annotations.Api;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
@Api(value = "Book Store", tags = "Inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{bookId}")
    public Inventory getInventoryByBookId(@PathVariable Integer bookId) {
        return inventoryService.getInventoryByBookId(bookId);
    }

    @PutMapping("/{bookId}")
    public Inventory updateStock(@PathVariable Integer bookId, @RequestParam int stock) {
        return inventoryService.updateStock(bookId, stock);
    }

    @PostMapping("/{bookId}/reserve")
    public boolean reserveStock(@PathVariable int bookId, @RequestParam int amount) {
        return inventoryService.reserveStock(bookId, amount);
    }

    @PostMapping("/{bookId}/release")
    public boolean releaseReservedStock(@PathVariable int bookId, @RequestParam int amount) {
        return inventoryService.releaseReservedStock(bookId, amount);
    }

    @PutMapping("/{bookId}/increase")
    public Inventory increaseStock(@PathVariable int bookId, @RequestParam int amount) {
        return inventoryService.increaseStock(bookId, amount);
    }

    @PutMapping("/{bookId}/reduce")
    public boolean reduceStock(@PathVariable int bookId, @RequestParam int amount) {
        return inventoryService.reduceStock(bookId, amount);
    }
}
