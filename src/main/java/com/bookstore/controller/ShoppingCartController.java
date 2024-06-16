package com.bookstore.controller;

import com.bookstore.model.ShoppingCart;
import com.bookstore.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/book-store/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @Autowired
    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping("/{userId}")
    public ShoppingCart getCart(@PathVariable String userId) {
        return shoppingCartService.getCartByUserId(userId);
    }

    @PostMapping("/{userId}/add")
    public ShoppingCart addToCart(@PathVariable String userId, @RequestBody ShoppingCart.CartItem item) {
        return shoppingCartService.addToCart(userId, item);
    }

    @DeleteMapping("/{userId}/remove/{bookId}")
    public ShoppingCart removeFromCart(@PathVariable String userId, @PathVariable String bookId) {
        return shoppingCartService.removeFromCart(userId, bookId);
    }

    @DeleteMapping("/{userId}/clear")
    public void clearCart(@PathVariable String userId) {
        shoppingCartService.clearCart(userId);
    }
}
