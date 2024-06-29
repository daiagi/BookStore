package com.bookstore.controller;

import com.bookstore.model.ShoppingCart;
import com.bookstore.service.ShoppingCartService;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/book-store/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping("/")
    public List<ShoppingCart> getAllCarts() {
        return shoppingCartService.findAll();
    }

    @GetMapping("/{userId}")
    public ShoppingCart getCart(@PathVariable String userId) {
        return shoppingCartService.getCartByUserId(userId);
    }



    @PostMapping("/{userId}/add")
    public ShoppingCart addToCart(@PathVariable String userId, @RequestBody ShoppingCart.CartItem item) {
        return shoppingCartService.addToCart(userId, item);
    }

    @PutMapping("/{userId}/update/{bookId}")
    public ShoppingCart updateItemQuantity(@PathVariable String userId, @PathVariable String bookId,
            @RequestParam int quantity) {
        return shoppingCartService.updateItemQuantity(userId, bookId, quantity);
    }

    @DeleteMapping("/{userId}/remove/{bookId}")
    public ShoppingCart removeFromCart(@PathVariable String userId, @PathVariable String bookId) {
        return shoppingCartService.removeFromCart(userId, bookId);
    }

    @GetMapping("/{userId}/count")
    public int getCartItemCount(@PathVariable String userId) {
        return shoppingCartService.getCartItemCount(userId);
    }

    @DeleteMapping("/{userId}/clear")
    public void clearCart(@PathVariable String userId) {
        shoppingCartService.clearCart(userId);
    }
}
