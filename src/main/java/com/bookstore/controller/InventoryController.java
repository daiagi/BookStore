package com.bookstore.controller;

import com.bookstore.model.Inventory;
import com.bookstore.service.InventoryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/book-store/inventory")
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
}
