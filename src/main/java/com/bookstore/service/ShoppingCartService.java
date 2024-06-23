package com.bookstore.service;

import com.bookstore.model.ShoppingCart;
import com.bookstore.repository.ShoppingCartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;

    @Autowired
    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
    }

    public List<ShoppingCart> findAll() {
        return shoppingCartRepository.findAll();
    }

    private ShoppingCart getOrCreateCart(String userId) {
        return shoppingCartRepository.findByUserId(userId).orElseGet(() -> {
            ShoppingCart newCart = new ShoppingCart();
            newCart.setUserId(userId);
            return newCart;
        });
    }

    public ShoppingCart getCartByUserId(String userId) {
        return getOrCreateCart(userId);
    }

    public ShoppingCart addToCart(String userId, ShoppingCart.CartItem item) {
        ShoppingCart cart = getOrCreateCart(userId);
        cart.getItems().add(item);
        return shoppingCartRepository.save(cart);
    }

    public ShoppingCart updateItemQuantity(String userId, String bookId, int quantity) {
        ShoppingCart cart = getCartByUserId(userId);
        cart.getItems().stream()
                .filter(item -> item.getBookId().equals(bookId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));
        return shoppingCartRepository.save(cart);
    }

    public ShoppingCart removeFromCart(String userId, String bookId) {
        ShoppingCart cart = getCartByUserId(userId);
        cart.setItems(cart.getItems().stream()
                .filter(item -> !item.getBookId().equals(bookId))
                .toList());
        return shoppingCartRepository.save(cart);
    }

    public int getCartItemCount(String userId) {
        return shoppingCartRepository.findByUserId(userId)
                .map(cart -> cart.getItems().stream().mapToInt(ShoppingCart.CartItem::getQuantity).sum())
                .orElse(0);
    }

    public void clearCart(String userId) {
        shoppingCartRepository.findByUserId(userId)
                .ifPresent(shoppingCartRepository::delete);
    }
}
